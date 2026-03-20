package vn.edu.uit.socialjob.platform.modules.skill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import vn.edu.uit.socialjob.platform.modules.skill.entity.SkillCategory;

public interface SkillCategoryRepository extends JpaRepository<SkillCategory, UUID> {
    @Query("SELECT sc FROM SkillCategory sc WHERE sc.slug = :slug AND sc.isDeleted = false")
    Optional<SkillCategory> findBySlug(@Param("slug") String slug);

    @Query("SELECT sc FROM SkillCategory sc WHERE sc.name = :name AND sc.isDeleted = false")
    Optional<SkillCategory> findByName(@Param("name") String name);

    @Query("SELECT sc FROM SkillCategory sc WHERE sc.isDeleted = false")
    List<SkillCategory> findAll();

    @Query("SELECT sc FROM SkillCategory sc WHERE sc.id = :id AND sc.isDeleted = false")
    Optional<SkillCategory> findById(@Param("id") UUID id);
}
