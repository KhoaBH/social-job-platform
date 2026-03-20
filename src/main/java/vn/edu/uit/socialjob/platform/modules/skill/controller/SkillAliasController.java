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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.edu.uit.socialjob.platform.modules.skill.dto.SkillAliasRequest;
import vn.edu.uit.socialjob.platform.modules.skill.entity.SkillAlias;
import vn.edu.uit.socialjob.platform.modules.skill.service.SkillAliasService;

@RestController
@RequestMapping("/api/skill-alias")
public class SkillAliasController {
    @Autowired
    private SkillAliasService skillAliasService;

    @GetMapping
    public ResponseEntity<List<SkillAlias>> listAll() {
        return ResponseEntity.ok(skillAliasService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillAlias> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(skillAliasService.getById(id));
    }

    @GetMapping("/skill/{skillId}")
    public ResponseEntity<List<SkillAlias>> getBySkillId(@PathVariable UUID skillId) {
        return ResponseEntity.ok(skillAliasService.getBySkillId(skillId));
    }

    @PostMapping
    public ResponseEntity<SkillAlias> create(
        @Valid @RequestBody SkillAliasRequest data
    ) {
        return ResponseEntity.ok(skillAliasService.create(data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillAlias> update(
        @PathVariable UUID id,
        @Valid @RequestBody SkillAliasRequest data
    ) {
        return ResponseEntity.ok(skillAliasService.update(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        skillAliasService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
