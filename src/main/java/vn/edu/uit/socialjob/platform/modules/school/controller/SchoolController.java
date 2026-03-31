package vn.edu.uit.socialjob.platform.modules.school.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.socialjob.platform.modules.school.dto.SchoolRequest;
import vn.edu.uit.socialjob.platform.modules.school.entity.School;
import vn.edu.uit.socialjob.platform.modules.school.service.SchoolService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schools")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    @GetMapping
    public ResponseEntity<List<School>> listAll() {
        return ResponseEntity.ok(schoolService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<School> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(schoolService.getById(id));
    }

    @PostMapping
    public ResponseEntity<School> create(@Valid @RequestBody SchoolRequest data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(schoolService.create(data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<School> update(@PathVariable UUID id, @Valid @RequestBody SchoolRequest data) {
        return ResponseEntity.ok(schoolService.update(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        schoolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
