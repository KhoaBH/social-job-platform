package vn.edu.uit.socialjob.platform.modules.skill.controller;

import java.util.List;
import java.util.UUID;

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

import jakarta.validation.Valid;
import vn.edu.uit.socialjob.platform.modules.skill.dto.CreateUserSkill;
import vn.edu.uit.socialjob.platform.modules.skill.dto.SkillRequest;
import vn.edu.uit.socialjob.platform.modules.skill.entity.Skill;
import vn.edu.uit.socialjob.platform.modules.skill.entity.UserSkill;
import vn.edu.uit.socialjob.platform.modules.skill.service.SkillService;
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

    @GetMapping("/{id}")
    public ResponseEntity<Skill> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(skillService.getById(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<UserSkill>> getByUser(@PathVariable UUID id) {
        return ResponseEntity.ok(skillService.getByUser(id));
    }
    
    @PostMapping
    public ResponseEntity<Skill> create(
        @Valid @RequestBody SkillRequest data
    ){
        return ResponseEntity.ok(skillService.create(data));    
    }

    @PostMapping("/user")
    public ResponseEntity<UserSkill> createUserSkill(@RequestBody CreateUserSkill createUserSkill, Authentication authentication) {
        UserSkill userSkill = skillService.createUserSkill(extractUserId(authentication), createUserSkill);
        return ResponseEntity.ok(userSkill);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Skill> update(
        @PathVariable UUID id,
        @Valid @RequestBody SkillRequest data
    ) {
        return ResponseEntity.ok(skillService.update(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        skillService.delete(id);
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
