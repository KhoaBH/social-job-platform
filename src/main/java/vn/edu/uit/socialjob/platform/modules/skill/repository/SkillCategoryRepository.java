package vn.edu.uit.socialjob.platform.modules.skill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import vn.edu.uit.socialjob.platform.modules.skill.entity.SkillCategory;

public interface SkillCategoryRepository extends JpaRepository<SkillCategory, java.util.UUID> {
    Optional<SkillCategory> findBySlug(String slug);
    Optional<SkillCategory> findByName(String name);
}
