package vn.edu.uit.socialjob.platform.modules.jobpost.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import vn.edu.uit.socialjob.platform.modules.jobpost.dto.JobPostRequest;
import vn.edu.uit.socialjob.platform.modules.jobpost.dto.JobPostWithSkillsRequest;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobPost;
import vn.edu.uit.socialjob.platform.modules.jobpost.service.JobPostService;
import vn.edu.uit.socialjob.platform.modules.jobpost.service.JobRecommendationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/job-posts")
public class JobPostController {


    private final JobPostService jobPostService;
    private final JobRecommendationService jobRecommendationService;
    
    public JobPostController(JobPostService jobPostService, JobRecommendationService jobRecommendationService) {
        this.jobPostService = jobPostService;
        this.jobRecommendationService = jobRecommendationService;
    }

    @GetMapping
    public ResponseEntity<List<JobPost>> listAll() {
        return ResponseEntity.ok(jobPostService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPost> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(jobPostService.getById(id));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<JobPost>> getByCompanyId(@PathVariable UUID companyId) {
        return ResponseEntity.ok(jobPostService.getByCompanyId(companyId));
    }

    @GetMapping("/posted-by/{postedById}")
    public ResponseEntity<List<JobPost>> getByPostedById(@PathVariable UUID postedById) {
        return ResponseEntity.ok(jobPostService.getByPostedById(postedById));
    }
    @GetMapping("/recommended")
    public ResponseEntity<List<JobPost>> getRecommendedJobs(Authentication authentication) {
        UUID userId = extractUserId(authentication);
        return ResponseEntity.ok(jobRecommendationService.getRecommendedJobs(userId));
    }

    @PostMapping
    public ResponseEntity<JobPost> create(@Valid @RequestBody JobPostWithSkillsRequest data, Authentication authentication) {
        UUID actorId = extractUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(jobPostService.createWithSkills(actorId, data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobPost> update(
        @PathVariable UUID id,
        @Valid @RequestBody JobPostRequest data
    ) {
        return ResponseEntity.ok(jobPostService.update(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        jobPostService.delete(id);
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