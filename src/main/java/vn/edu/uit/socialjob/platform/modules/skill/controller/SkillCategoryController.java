package vn.edu.uit.socialjob.platform.modules.skill.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import vn.edu.uit.socialjob.platform.modules.skill.dto.SkillCategoryRequest;
import vn.edu.uit.socialjob.platform.modules.skill.entity.SkillCategory;
import vn.edu.uit.socialjob.platform.modules.skill.service.SkillCategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/skill-categories")
public class SkillCategoryController {
    @Autowired
    private SkillCategoryService skillCategoryService;

    @GetMapping
    public ResponseEntity<List<SkillCategory>> listAll() {
        return ResponseEntity.ok(skillCategoryService.getAll());
    }

    @PostMapping
    public ResponseEntity<SkillCategory> create(
        @Valid @RequestBody SkillCategoryRequest data
    ){
        return ResponseEntity.ok(skillCategoryService.create(data));    
    }
    
}
