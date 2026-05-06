package vn.edu.uit.socialjob.platform.modules.jobpost.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import vn.edu.uit.socialjob.platform.common.enums.JobApplicationStatus;
import vn.edu.uit.socialjob.platform.modules.jobpost.dto.JobApplicationRequest;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobApplication;
import vn.edu.uit.socialjob.platform.modules.jobpost.service.JobApplicationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/job-posts/{jobPostId}/applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping
    public ResponseEntity<List<JobApplication>> listByJobPostId(@PathVariable UUID jobPostId) {
        return ResponseEntity.ok(jobApplicationService.getByJobPostId(jobPostId));
    }

    @GetMapping("/mine/{userId}")
    public ResponseEntity<List<JobApplication>> listByApplicantId(@PathVariable UUID userId) {
        return ResponseEntity.ok(jobApplicationService.getByApplicantId(userId));
    }

    @PostMapping
    public ResponseEntity<JobApplication> apply(
        @PathVariable UUID jobPostId,
        @Valid @RequestBody JobApplicationRequest data,
        Authentication authentication
    ) {
        UUID userId = extractUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(jobApplicationService.apply(userId, jobPostId, data));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<JobApplication> updateStatus(
        @PathVariable UUID id,
        @RequestBody JobApplicationStatus status
    ) {
        return ResponseEntity.ok(jobApplicationService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        jobApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UUID extractUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}