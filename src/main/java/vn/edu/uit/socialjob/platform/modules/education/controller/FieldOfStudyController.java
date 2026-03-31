package vn.edu.uit.socialjob.platform.modules.education.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import vn.edu.uit.socialjob.platform.modules.education.dto.FieldOfStudyRequest;
import vn.edu.uit.socialjob.platform.modules.education.entity.FieldOfStudy;
import vn.edu.uit.socialjob.platform.modules.education.service.FieldOfStudyService;

@RestController
@RequestMapping("/api/field-of-studies")
public class FieldOfStudyController {
    @Autowired
    private FieldOfStudyService fieldOfStudyService;

    @GetMapping
    public ResponseEntity<List<FieldOfStudy>> listAll() {
        return ResponseEntity.ok(fieldOfStudyService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FieldOfStudy> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(fieldOfStudyService.getById(id));
    }

    @PostMapping
    public ResponseEntity<FieldOfStudy> create(
        @Valid @RequestBody FieldOfStudyRequest data
    ){
        return ResponseEntity.ok(fieldOfStudyService.create(data));    
    }

    @PutMapping("/{id}")
    public ResponseEntity<FieldOfStudy> update(
        @PathVariable UUID id,
        @Valid @RequestBody FieldOfStudyRequest data
    ) {
        return ResponseEntity.ok(fieldOfStudyService.update(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        fieldOfStudyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
