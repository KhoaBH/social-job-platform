package vn.edu.uit.socialjob.platform.modules.experience.controller;

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
import vn.edu.uit.socialjob.platform.modules.experience.dto.WorkExperienceRequest;
import vn.edu.uit.socialjob.platform.modules.experience.entity.WorkExperience;
import vn.edu.uit.socialjob.platform.modules.experience.service.WorkExperienceService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/work-experiences")
public class WorkExperienceController {

    @Autowired
    private WorkExperienceService workExperienceService;

    @GetMapping
    public ResponseEntity<List<WorkExperience>> listAll() {
        return ResponseEntity.ok(workExperienceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkExperience> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(workExperienceService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WorkExperience>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(workExperienceService.getByUser(userId));
    }

    @PostMapping
    public ResponseEntity<WorkExperience> create(
        @Valid @RequestBody WorkExperienceRequest data,
        Authentication authentication
    ) {
        UUID userId = extractUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(workExperienceService.create(userId, data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkExperience> update(
        @PathVariable UUID id,
        @Valid @RequestBody WorkExperienceRequest data,
        Authentication authentication
    ) {
        UUID actorId = extractUserId(authentication);
        return ResponseEntity.ok(workExperienceService.update(id, actorId, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        UUID actorId = extractUserId(authentication);
        workExperienceService.delete(id, actorId);
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
