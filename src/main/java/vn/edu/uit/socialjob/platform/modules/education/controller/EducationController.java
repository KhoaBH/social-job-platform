package vn.edu.uit.socialjob.platform.modules.education.controller;

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
import vn.edu.uit.socialjob.platform.modules.education.dto.EducationRequest;
import vn.edu.uit.socialjob.platform.modules.education.entity.Education;
import vn.edu.uit.socialjob.platform.modules.education.service.EducationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/educations")
public class EducationController {

    @Autowired
    private EducationService educationService;

    @GetMapping
    public ResponseEntity<List<Education>> listAll() {
        return ResponseEntity.ok(educationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Education> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(educationService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Education>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(educationService.getByUser(userId));
    }

    @PostMapping
    public ResponseEntity<Education> create(@Valid @RequestBody EducationRequest data, Authentication authentication) {
        UUID userId = extractUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(educationService.create(userId, data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Education> update(
        @PathVariable UUID id,
        @Valid @RequestBody EducationRequest data,
        Authentication authentication
    ) {
        UUID actorId = extractUserId(authentication);
        return ResponseEntity.ok(educationService.update(id, actorId, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        UUID actorId = extractUserId(authentication);
        educationService.delete(id, actorId);
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
