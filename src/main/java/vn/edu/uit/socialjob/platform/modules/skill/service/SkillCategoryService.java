package vn.edu.uit.socialjob.platform.modules.skill.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.edu.uit.socialjob.platform.modules.skill.repository.SkillCategoryRepository;
import java.util.List;
import java.util.UUID;

import vn.edu.uit.socialjob.platform.modules.skill.dto.SkillCategoryRequest;
import vn.edu.uit.socialjob.platform.modules.skill.entity.SkillCategory;

@Service
public class SkillCategoryService {
    @Autowired
    private SkillCategoryRepository skillCategoryRepository;

    public List<SkillCategory> getAll() {
        return skillCategoryRepository.findAll();
    }

    public SkillCategory getById(UUID id) {
        return skillCategoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Skill category not found"));
    }

    public SkillCategory create(SkillCategoryRequest data) {
        String name = data.getName().trim();
        String slug = name.toLowerCase().replaceAll("\\s+", "-");
        SkillCategory skillCategory = new SkillCategory();
        skillCategory.setName(name);
        skillCategory.setDescription(data.getDescription().trim());
        skillCategory.setSlug(slug);

        return skillCategoryRepository.save(skillCategory);
    }

    public SkillCategory update(UUID id, SkillCategoryRequest data) {
        SkillCategory skillCategory = this.getById(id);
        String name = data.getName().trim();
        String slug = name.toLowerCase().replaceAll("\\s+", "-");
        
        skillCategory.setName(name);
        skillCategory.setDescription(data.getDescription().trim());
        skillCategory.setSlug(slug);

        return skillCategoryRepository.save(skillCategory);
    }

    public void delete(UUID id) {
        SkillCategory skillCategory = this.getById(id);
        skillCategory.setDeleted(true);
        skillCategoryRepository.save(skillCategory);
    }
}
