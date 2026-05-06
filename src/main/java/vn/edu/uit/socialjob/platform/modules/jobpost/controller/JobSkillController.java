package vn.edu.uit.socialjob.platform.modules.jobpost.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.socialjob.platform.modules.jobpost.dto.JobSkillRequest;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobSkill;
import vn.edu.uit.socialjob.platform.modules.jobpost.service.JobSkillService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/job-posts/{jobPostId}/skills")
public class JobSkillController {

    private final JobSkillService jobSkillService;

    public JobSkillController(JobSkillService jobSkillService) {
        this.jobSkillService = jobSkillService;
    }

    @GetMapping
    public ResponseEntity<List<JobSkill>> listByJobPostId(@PathVariable UUID jobPostId) {
        return ResponseEntity.ok(jobSkillService.getByJobPostId(jobPostId));
    }

    @PostMapping
    public ResponseEntity<JobSkill> create(
        @PathVariable UUID jobPostId,
        @Valid @RequestBody JobSkillRequest data
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobSkillService.create(jobPostId, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        jobSkillService.delete(id);
        return ResponseEntity.noContent().build();
    }
}