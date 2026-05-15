package vn.edu.uit.socialjob.platform.modules.apply.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import vn.edu.uit.socialjob.platform.modules.apply.dto.ApplyResponse;
import vn.edu.uit.socialjob.platform.modules.apply.service.ApplyService;

@RestController
@RequestMapping("/api")
public class ApplyController {

    private final ApplyService applyService;

    public ApplyController(ApplyService applyService) {
        this.applyService = applyService;
    }

    @GetMapping("/job-posts/{jobPostId}/apply")
    public ResponseEntity<List<ApplyResponse>> getByJobPostId(@PathVariable UUID jobPostId) {
        return ResponseEntity.ok(applyService.getByJobPostId(jobPostId));
    }

    @GetMapping("/job-posts/{jobPostId}/apply/me")
    public ResponseEntity<ApplyResponse> getMyApply(
        @PathVariable UUID jobPostId,
        Authentication authentication
    ) {
        UUID userId = extractUserId(authentication);
        return ResponseEntity.ok(applyService.getMyApply(userId, jobPostId));
    }

    @GetMapping("/applies/me")
    public ResponseEntity<List<ApplyResponse>> getMyApplies(Authentication authentication) {
        UUID userId = extractUserId(authentication);
        return ResponseEntity.ok(applyService.getMyApplies(userId));
    }

    @PostMapping(value = "/job-posts/{jobPostId}/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApplyResponse> apply(
        @PathVariable UUID jobPostId,
        @RequestParam("file") MultipartFile file,
        Authentication authentication
    ) {
        UUID userId = extractUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(applyService.apply(userId, jobPostId, file));
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
