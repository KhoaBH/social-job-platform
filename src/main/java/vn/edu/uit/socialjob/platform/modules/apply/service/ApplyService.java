package vn.edu.uit.socialjob.platform.modules.apply.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.edu.uit.socialjob.platform.common.service.AzureBlobStorageService;
import vn.edu.uit.socialjob.platform.modules.apply.dto.ApplyResponse;
import vn.edu.uit.socialjob.platform.modules.apply.entity.Apply;
import vn.edu.uit.socialjob.platform.modules.apply.repository.ApplyRepository;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobPost;
import vn.edu.uit.socialjob.platform.modules.jobpost.repository.JobPostRepository;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.repository.UserRepository;

@Service
public class ApplyService {

    private final ApplyRepository applyRepository;
    private final JobPostRepository jobPostRepository;
    private final UserRepository userRepository;
    private final AzureBlobStorageService azureBlobStorageService;

    public ApplyService(
        ApplyRepository applyRepository,
        JobPostRepository jobPostRepository,
        UserRepository userRepository,
        AzureBlobStorageService azureBlobStorageService
    ) {
        this.applyRepository = applyRepository;
        this.jobPostRepository = jobPostRepository;
        this.userRepository = userRepository;
        this.azureBlobStorageService = azureBlobStorageService;
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

        azureBlobStorageService.upload(file, "applies");

        Apply apply = new Apply();
        apply.setJobPost(jobPost);
        apply.setUser(user);
        apply.setFileName(resolveFileName(file));

        Apply saved = applyRepository.save(apply);
        return mapToResponse(saved);
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
            .fileName(apply.getFileName())
            .createdAt(apply.getCreatedAt())
            .build();
    }
}
