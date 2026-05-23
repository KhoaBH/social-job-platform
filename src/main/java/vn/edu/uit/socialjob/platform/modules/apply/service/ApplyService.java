package vn.edu.uit.socialjob.platform.modules.apply.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.edu.uit.socialjob.platform.common.service.AzureBlobStorageService;
import vn.edu.uit.socialjob.platform.modules.apply.dto.ApplyResponse;
import vn.edu.uit.socialjob.platform.modules.apply.entity.Apply;
import vn.edu.uit.socialjob.platform.modules.apply.repository.ApplyRepository;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobPost;
import vn.edu.uit.socialjob.platform.modules.jobpost.repository.JobPostRepository;
import vn.edu.uit.socialjob.platform.modules.jobpost.service.JobSkillService;
import vn.edu.uit.socialjob.platform.modules.skill.repository.UserSkillRepository;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.repository.UserRepository;

@Service
public class ApplyService {

    private static final Logger logger = LoggerFactory.getLogger(ApplyService.class);

    private final ApplyRepository applyRepository;
    private final JobPostRepository jobPostRepository;
    private final UserRepository userRepository;
    private final AzureBlobStorageService azureBlobStorageService;
    private final vn.edu.uit.socialjob.platform.modules.jobpost.service.JobEmbeddingClient jobEmbeddingClient;
    private final UserSkillRepository userSkillRepository;
    private final JobSkillService jobSkillService;
    private final BertServiceClient bertServiceClient;

    public ApplyService(
        ApplyRepository applyRepository,
        JobPostRepository jobPostRepository,
        UserRepository userRepository,
        AzureBlobStorageService azureBlobStorageService,
        vn.edu.uit.socialjob.platform.modules.jobpost.service.JobEmbeddingClient jobEmbeddingClient,
        UserSkillRepository userSkillRepository,
        JobSkillService jobSkillService,
        BertServiceClient bertServiceClient
    ) {
        this.applyRepository = applyRepository;
        this.jobPostRepository = jobPostRepository;
        this.userRepository = userRepository;
        this.azureBlobStorageService = azureBlobStorageService;
        this.jobEmbeddingClient = jobEmbeddingClient;
        this.userSkillRepository = userSkillRepository;
        this.jobSkillService = jobSkillService;
        this.bertServiceClient = bertServiceClient;
    }

    public ApplyResponse apply(UUID userId, UUID jobPostId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        if (applyRepository.findActiveByJobPostIdAndUserId(jobPostId, userId).isPresent()) {
            throw new IllegalArgumentException("You already applied for this job");
        }

        JobPost jobPost = jobPostRepository.findById(jobPostId)
            .orElseThrow(() -> new IllegalArgumentException("Job post not found"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String fileUrl = azureBlobStorageService.upload(file, "applies");

        Apply apply = new Apply();
        apply.setJobPost(jobPost);
        apply.setUser(user);
        apply.setFileName(resolveFileName(file));
        apply.setFileUrl(fileUrl);

        Apply saved = applyRepository.save(apply);
        
        // Score CV using BERT service after saving
        try {
            scoreCvAsync(saved);
        } catch (Exception ex) {
            logger.error("Failed to score CV for apply id={}: {}", saved.getId(), ex.getMessage());
            // Don't throw exception - continue with apply even if scoring fails
        }
        
        // Metadata updates are temporarily disabled; skipping apply_count update
        return mapToResponse(saved);
    }

    /**
     * Score the CV using BERT service and update Apply entity with score.
     * This is called asynchronously to avoid blocking the apply request.
     */
    private void scoreCvAsync(Apply apply) {
        // Get user skills
        List<String> userSkillIds = userSkillRepository.findByUserId(apply.getUser().getId())
            .stream()
            .map(us -> us.getSkill().getId().toString())
            .toList();

        // Get job required skills
        List<String> jobSkillIds = jobSkillService.getByJobPostId(apply.getJobPost().getId())
            .stream()
            .map(js -> js.getSkill().getId().toString())
            .toList();

        // Call BERT service to score CV
        Double score = bertServiceClient.scoreCv(
            apply.getFileUrl(),
            apply.getJobPost().getId().toString(),
            userSkillIds,
            jobSkillIds
        );

        // Update Apply with score
        apply.setScore(score);
        apply.setScoreUpdatedAt(LocalDateTime.now());
        applyRepository.save(apply);
        
        logger.info("CV scored for apply id={}: score={}", apply.getId(), score);
    }

    public List<ApplyResponse> getByJobPostId(UUID jobPostId) {
        return applyRepository.findAllActiveByJobPostId(jobPostId)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    public ApplyResponse getMyApply(UUID userId, UUID jobPostId) {
        Apply apply = applyRepository.findActiveByJobPostIdAndUserId(jobPostId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Apply not found"));

        return mapToResponse(apply);
    }

    public List<ApplyResponse> getMyApplies(UUID userId) {
        return applyRepository.findAllActiveByUserId(userId)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    private String resolveFileName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("File name is invalid");
        }

        return originalFileName;
    }

    private ApplyResponse mapToResponse(Apply apply) {
        return ApplyResponse.builder()
            .id(apply.getId())
            .jobPostId(apply.getJobPost().getId())
            .userId(apply.getUser().getId())
            .userFullName(apply.getUser().getFullName())
            .userEmail(apply.getUser().getEmail())
            .fileName(apply.getFileName())
            .fileUrl(apply.getFileUrl())
            .score(apply.getScore())
            .scoreUpdatedAt(apply.getScoreUpdatedAt())
            .createdAt(apply.getCreatedAt())
            .build();
    }
}

