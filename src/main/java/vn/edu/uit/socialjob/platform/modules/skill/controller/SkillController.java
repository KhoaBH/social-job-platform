package vn.edu.uit.socialjob.platform.modules.skill.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import vn.edu.uit.socialjob.platform.modules.skill.dto.SkillRequest;
import vn.edu.uit.socialjob.platform.modules.skill.entity.Skill;
import vn.edu.uit.socialjob.platform.modules.skill.service.SkillService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/skills")
public class SkillController {
    @Autowired
    private SkillService skillService;

    @GetMapping
    public ResponseEntity<List<Skill>> listAll() {
        return ResponseEntity.ok(skillService.getAll());
    }
    @PostMapping
    public ResponseEntity<Skill> create(
        @Valid @RequestBody SkillRequest data
    ){
        return ResponseEntity.ok(skillService.create(data));    
    }
    
}
