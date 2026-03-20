package vn.edu.uit.socialjob.platform.modules.skill.controller;

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
import vn.edu.uit.socialjob.platform.modules.skill.dto.SkillCategoryRequest;
import vn.edu.uit.socialjob.platform.modules.skill.entity.SkillCategory;
import vn.edu.uit.socialjob.platform.modules.skill.service.SkillCategoryService;

@RestController
@RequestMapping("/api/skill-categories")
public class SkillCategoryController {
    @Autowired
    private SkillCategoryService skillCategoryService;

    @GetMapping
    public ResponseEntity<List<SkillCategory>> listAll() {
        return ResponseEntity.ok(skillCategoryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillCategory> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(skillCategoryService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SkillCategory> create(
        @Valid @RequestBody SkillCategoryRequest data
    ){
        return ResponseEntity.ok(skillCategoryService.create(data));    
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillCategory> update(
        @PathVariable UUID id,
        @Valid @RequestBody SkillCategoryRequest data
    ) {
        return ResponseEntity.ok(skillCategoryService.update(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        skillCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
