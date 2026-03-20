package vn.edu.uit.socialjob.platform.modules.skill.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.edu.uit.socialjob.platform.modules.skill.dto.SkillAliasRequest;
import vn.edu.uit.socialjob.platform.modules.skill.entity.Skill;
import vn.edu.uit.socialjob.platform.modules.skill.entity.SkillAlias;
import vn.edu.uit.socialjob.platform.modules.skill.repository.SkillAliasRepository;
import vn.edu.uit.socialjob.platform.modules.skill.repository.SkillRepository;

@Service
public class SkillAliasService {
    @Autowired
    private SkillAliasRepository skillAliasRepository;

    @Autowired
    private SkillRepository skillRepository;

    public List<SkillAlias> getAll() {
        return this.skillAliasRepository.findAll();
    }

    public List<SkillAlias> getBySkillId(UUID skillId) {
        return this.skillAliasRepository.findBySkillId(skillId);
    }

    public SkillAlias create(SkillAliasRequest data) {
        String alias = data.getAlias().trim();
        
        Skill skill = skillRepository.findById(data.getSkillId())
            .orElseThrow(() -> new IllegalArgumentException("Skill not found"));
        
        SkillAlias skillAlias = new SkillAlias();
        skillAlias.setAlias(alias);
        skillAlias.setSkill(skill);

        return skillAliasRepository.save(skillAlias);
    }

    public SkillAlias getById(UUID id) {
        return this.skillAliasRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Skill alias not found"));
    }

    public SkillAlias update(UUID id, SkillAliasRequest data) {
        SkillAlias skillAlias = this.getById(id);
        String alias = data.getAlias().trim();
        
        Skill skill = skillRepository.findById(data.getSkillId())
            .orElseThrow(() -> new IllegalArgumentException("Skill not found"));
        
        skillAlias.setAlias(alias);
        skillAlias.setSkill(skill);
        
        return skillAliasRepository.save(skillAlias);
    }

    public void delete(UUID id) {
        SkillAlias skillAlias = this.getById(id);
        skillAlias.setDeleted(true);
        skillAliasRepository.save(skillAlias);
    }
}
