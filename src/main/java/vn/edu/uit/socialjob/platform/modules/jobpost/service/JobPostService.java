package vn.edu.uit.socialjob.platform.modules.jobpost.service;

import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.company.entity.Company;
import vn.edu.uit.socialjob.platform.modules.company.repository.CompanyRepository;
import vn.edu.uit.socialjob.platform.modules.jobpost.dto.JobPostRequest;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobPost;
import vn.edu.uit.socialjob.platform.modules.jobpost.repository.JobPostRepository;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class JobPostService {

    private final JobPostRepository jobPostRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public JobPostService(
        JobPostRepository jobPostRepository,
        CompanyRepository companyRepository,
        UserRepository userRepository
    ) {
        this.jobPostRepository = jobPostRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public List<JobPost> getAll() {
        return jobPostRepository.findAll();
    }

    public JobPost getById(UUID id) {
        return jobPostRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Job post not found"));
    }

    public List<JobPost> getByCompanyId(UUID companyId) {
        return jobPostRepository.findByCompanyId(companyId);
    }

    public List<JobPost> getByPostedById(UUID postedById) {
        return jobPostRepository.findByPostedById(postedById);
    }

    public JobPost create(UUID actorId, JobPostRequest data) {
        Company company = companyRepository.findById(data.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found"));
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
        jobPost.setLocation(data.getLocation());
        if (data.getStatus() != null) {
            jobPost.setStatus(data.getStatus());
        }

        return jobPostRepository.save(jobPost);
    }

    public JobPost update(UUID id, JobPostRequest data) {
        JobPost jobPost = getById(id);
        Company company = companyRepository.findById(data.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        jobPost.setCompany(company);
        jobPost.setTitle(data.getTitle().trim());
        jobPost.setDescription(data.getDescription());
        jobPost.setExperienceLevel(data.getExperienceLevel());
        jobPost.setSalaryMin(data.getSalaryMin());
        jobPost.setSalaryMax(data.getSalaryMax());
        jobPost.setLocation(data.getLocation());
        if (data.getStatus() != null) {
            jobPost.setStatus(data.getStatus());
        }

        return jobPostRepository.save(jobPost);
    }

    public void delete(UUID id) {
        JobPost jobPost = getById(id);
        jobPost.setDeleted(true);
        jobPostRepository.save(jobPost);
    }
}