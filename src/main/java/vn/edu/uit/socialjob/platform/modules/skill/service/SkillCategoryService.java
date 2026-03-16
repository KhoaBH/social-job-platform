package vn.edu.uit.socialjob.platform.modules.skill.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.edu.uit.socialjob.platform.modules.skill.repository.SkillCategoryRepository;
import java.util.List;

import vn.edu.uit.socialjob.platform.modules.skill.dto.SkillCategoryRequest;
import vn.edu.uit.socialjob.platform.modules.skill.entity.SkillCategory;
@Service
public class SkillCategoryService {
    @Autowired
    private SkillCategoryRepository skillCategoryRepository;
    public List<SkillCategory> getAll() {
        return skillCategoryRepository.findAll();
    }
    public SkillCategory create(SkillCategoryRequest data) {
        String slug = data.getName().toLowerCase().replaceAll("\\s+", "-");
        SkillCategory skillCategory = new SkillCategory();
        skillCategory.setName(data.getName());
        skillCategory.setDescription(data.getDescription());
        skillCategory.setSlug(slug);

        return skillCategoryRepository.save(skillCategory);
    }
}
