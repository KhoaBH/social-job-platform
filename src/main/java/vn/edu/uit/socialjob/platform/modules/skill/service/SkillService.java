package vn.edu.uit.socialjob.platform.modules.skill.service;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.edu.uit.socialjob.platform.modules.skill.dto.CreateUserSkill;
import vn.edu.uit.socialjob.platform.modules.skill.dto.SkillRequest;
import vn.edu.uit.socialjob.platform.modules.skill.entity.Skill;
import vn.edu.uit.socialjob.platform.modules.skill.entity.SkillCategory;
import vn.edu.uit.socialjob.platform.modules.skill.entity.UserSkill;
import vn.edu.uit.socialjob.platform.modules.skill.repository.SkillCategoryRepository;
import vn.edu.uit.socialjob.platform.modules.skill.repository.SkillRepository;
import vn.edu.uit.socialjob.platform.modules.skill.repository.UserSkillRepository;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.service.UserService;

@Service
public class SkillService {
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private SkillCategoryRepository skillCategoryRepository;
    @Autowired
    private UserService userRepository;
    @Autowired
    private UserSkillRepository userSkillRepository;
    public List<Skill> getAll() {
        return this.skillRepository.findAll();
    }

    public Skill create(SkillRequest data) {
        String name = data.getName().trim();
        String nameNomorlizeString = name.toLowerCase().replaceAll("\\s+", "-");
        Skill skill = new Skill();
        SkillCategory category = resolveCategory(data.getCategoryId());
        skill.setName(name);
        skill.setNameNormalized(nameNomorlizeString);
        skill.setCategory(category);

        return skillRepository.save(skill);
    }

    
    public List<UserSkill> getByUser(UUID id) {
        return userSkillRepository.findByUserId(id);
    }
    public Skill getById(UUID id) {
        return this.skillRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Skill not found"));
    }
    public UserSkill createUserSkill(UUID userId, CreateUserSkill data) {
        User user = userRepository.getById(userId);
        Skill skill = this.getById(data.getSkillId());
        UserSkill userSkill = userSkillRepository.findByUserIdAndSkillId(userId, data.getSkillId())
            .orElseGet(UserSkill::new);

        userSkill.setUser(user);
        userSkill.setSkill(skill);
        userSkill.setLevel(data.getLevel());
        userSkill.setDeleted(false);
        return userSkillRepository.save(userSkill);
    }

    public Skill update(UUID id, SkillRequest data) {
        Skill skill = this.getById(id);
        String name = data.getName().trim();
        String nameNormalizedString = name.toLowerCase().replaceAll("\\s+", "-");
        SkillCategory category = resolveCategory(data.getCategoryId());
        
        skill.setName(name);
        skill.setNameNormalized(nameNormalizedString);
        skill.setCategory(category);

        return skillRepository.save(skill);
    }
    public UserSkill updateUserSkill(UUID id, CreateUserSkill data) {
        UserSkill userSkill = userSkillRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User skill not found"));
        Skill skill = this.getById(data.getSkillId());
        userSkill.setSkill(skill);
        userSkill.setLevel(data.getLevel());
        return userSkillRepository.save(userSkill);
    }

    private SkillCategory resolveCategory(UUID categoryId) {
        if (categoryId == null) {
            return null;
        }

        return skillCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Skill category not found"));
    }

    public void delete(UUID id) {
        Skill skill = this.getById(id);
        skill.setDeleted(true);
        skillRepository.save(skill);
    }
    public void deleteUserSkill(UUID id) {
        UserSkill userSkill = userSkillRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User skill not found"));
        userSkill.setDeleted(true);
        userSkillRepository.save(userSkill);
    }
}