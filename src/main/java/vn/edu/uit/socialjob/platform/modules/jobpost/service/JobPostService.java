package vn.edu.uit.socialjob.platform.modules.jobpost.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.uit.socialjob.platform.modules.apply.repository.ApplyRepository;
import vn.edu.uit.socialjob.platform.modules.company.entity.Company;
import vn.edu.uit.socialjob.platform.modules.company.repository.CompanyRepository;
import vn.edu.uit.socialjob.platform.modules.company.service.CompanyUserService;
import vn.edu.uit.socialjob.platform.modules.jobpost.dto.JobPostRequest;
import vn.edu.uit.socialjob.platform.modules.jobpost.dto.JobPostWithSkillsRequest;
import vn.edu.uit.socialjob.platform.modules.jobpost.dto.JobSkillRequest;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobPost;
import vn.edu.uit.socialjob.platform.modules.jobpost.repository.JobPostRepository;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JobPostService {

    private static final Logger logger = LoggerFactory.getLogger(JobPostService.class);

    private final JobPostRepository jobPostRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final JobSkillService jobSkillService;
    private final JobEmbeddingClient jobEmbeddingClient;
    private final ApplyRepository applyRepository;
    private final CompanyUserService companyUserService;
    public JobPostService(
        JobPostRepository jobPostRepository,
        CompanyRepository companyRepository,
        UserRepository userRepository,
        JobSkillService jobSkillService,
        JobEmbeddingClient jobEmbeddingClient,
        ApplyRepository applyRepository,
        CompanyUserService companyUserService
    ) {
        this.jobPostRepository = jobPostRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.jobSkillService = jobSkillService;
        this.jobEmbeddingClient = jobEmbeddingClient;
        this.applyRepository = applyRepository;
        this.companyUserService = companyUserService;
    }

    public List<JobPost> getAll() {
        return jobPostRepository.findAll();
    }

    public JobPost getById(UUID id) {
        return jobPostRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Job post not found"));
    }

    public List<JobPost> getByCompanyId(UUID companyId) {
        List<JobPost> posts = jobPostRepository.findByCompanyId(companyId);
        List<UUID> jobPostIds = posts.stream().map(JobPost::getId).toList();
        Map<UUID, Long> applyCounts = jobPostIds.isEmpty()
            ? Map.of()
            : applyRepository.countActiveByJobPostIds(jobPostIds).stream()
                .collect(Collectors.toMap(
                    ApplyRepository.JobPostApplyCountView::getJobPostId,
                    view -> view.getTotal() == null ? 0L : view.getTotal()
                ));

        posts.forEach(post -> post.setApplications(applyCounts.getOrDefault(post.getId(), 0L).intValue()));
        return posts;

    }

    public List<JobPost> getByPostedById(UUID postedById) {
        return jobPostRepository.findByPostedById(postedById);
    }

    public JobPost create(UUID actorId, JobPostRequest data) {
        JobPost jobPost = persistJobPost(actorId, data);
        jobEmbeddingClient.sendEmbeddingAfterCommit(jobPost.getId(), data);
        // send posted_at and apply_count metadata after commit
        Map<String, Object> metadata = new java.util.LinkedHashMap<>();
        if (jobPost.getCreatedAt() != null) {
            metadata.put("date_create", jobPost.getCreatedAt().toString());
        }
        // initial apply count
        int applyCount = applyRepository.findAllActiveByJobPostId(jobPost.getId()).size();
        metadata.put("apply_count", applyCount);
        jobEmbeddingClient.sendMetadataUpdateAfterCommit(jobPost.getId(), metadata);
        return jobPost;
    }

    private JobPost persistJobPost(UUID actorId, JobPostRequest data) {
        Company company = companyRepository.findById(data.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found"));
        if (!companyUserService.canManageRecruitment(company.getId(), actorId)) {
            throw new IllegalArgumentException("Only company owner or manager can create job posts");
        }
        User postedBy = userRepository.findById(actorId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        JobPost jobPost = new JobPost();
        jobPost.setCompany(company);
        jobPost.setPostedBy(postedBy);
        jobPost.setTitle(data.getTitle().trim());
        jobPost.setDescription(data.getDescription());
        jobPost.setExperienceLevel(data.getExperienceLevel());
        jobPost.setSalaryMin(data.getSalaryMin());
        jobPost.setSalaryMax(data.getSalaryMax());
        logger.debug("Persisting JobPost - title={}, salaryMin={}, salaryMax={}", data.getTitle(), data.getSalaryMin(), data.getSalaryMax());
        jobPost.setLocation(data.getLocation());
        if (data.getStatus() != null) {
            jobPost.setStatus(data.getStatus());
        }

        return jobPostRepository.save(jobPost);
    }

    @Transactional
    public JobPost createWithSkills(UUID actorId, JobPostWithSkillsRequest data) {
        // Create job post first
        JobPost jobPost = persistJobPost(actorId, data);

        // Attach skills if any
        if (data.getSkills() != null && !data.getSkills().isEmpty()) {
            for (JobSkillRequest skillReq : data.getSkills()) {
                jobSkillService.create(jobPost.getId(), skillReq);
            }
        }

        jobEmbeddingClient.sendEmbeddingAfterCommit(jobPost.getId(), data, data.getSkills());
        // Metadata updates disabled: not scheduling posted_at/apply_count payload

        return jobPost;
    }

    public JobPost update(UUID id, UUID actorId, JobPostRequest data) {
        JobPost jobPost = getById(id);
        Company company = companyRepository.findById(data.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found"));
        if (!companyUserService.canManageRecruitment(company.getId(), actorId)) {
            throw new IllegalArgumentException("Only company owner or manager can update job posts");
        }

        jobPost.setCompany(company);
        jobPost.setTitle(data.getTitle().trim());
        jobPost.setDescription(data.getDescription());
        jobPost.setExperienceLevel(data.getExperienceLevel());
        jobPost.setSalaryMin(data.getSalaryMin());
        jobPost.setSalaryMax(data.getSalaryMax());
        logger.debug("Updating JobPost id={} - salaryMin={}, salaryMax={}", id, data.getSalaryMin(), data.getSalaryMax());
        jobPost.setLocation(data.getLocation());
        if (data.getStatus() != null) {
            jobPost.setStatus(data.getStatus());
        }

        JobPost savedJobPost = jobPostRepository.save(jobPost);
        jobEmbeddingClient.sendEmbeddingAfterCommit(savedJobPost.getId(), data);
        Map<String, Object> metadata = new java.util.LinkedHashMap<>();
        if (savedJobPost.getCreatedAt() != null) {
            metadata.put("date_create", savedJobPost.getCreatedAt().toString());
        }
        int applyCount = applyRepository.findAllActiveByJobPostId(savedJobPost.getId()).size();
        metadata.put("apply_count", applyCount);
        jobEmbeddingClient.sendMetadataUpdateAfterCommit(savedJobPost.getId(), metadata);
        return savedJobPost;
    }

    public void delete(UUID id, UUID actorId) {
        JobPost jobPost = getById(id);
        if (!companyUserService.canManageRecruitment(jobPost.getCompany().getId(), actorId)) {
            throw new IllegalArgumentException("Only company owner or manager can delete job posts");
        }
        jobPost.setDeleted(true);
        jobPostRepository.save(jobPost);
    }
}