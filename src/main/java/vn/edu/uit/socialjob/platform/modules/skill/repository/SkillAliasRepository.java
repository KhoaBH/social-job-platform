package vn.edu.uit.socialjob.platform.modules.skill.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.uit.socialjob.platform.modules.skill.entity.SkillAlias;

public interface SkillAliasRepository extends JpaRepository<SkillAlias, UUID> {
    @Query("SELECT sa FROM SkillAlias sa WHERE sa.alias = :alias AND sa.isDeleted = false")
    Optional<SkillAlias> findByAlias(@Param("alias") String alias);

    @Query("SELECT sa FROM SkillAlias sa WHERE sa.skill.id = :skillId AND sa.isDeleted = false")
    List<SkillAlias> findBySkillId(@Param("skillId") UUID skillId);

    @Query("SELECT sa FROM SkillAlias sa WHERE sa.isDeleted = false")
    List<SkillAlias> findAll();

    @Query("SELECT sa FROM SkillAlias sa WHERE sa.id = :id AND sa.isDeleted = false")
    Optional<SkillAlias> findById(@Param("id") UUID id);
}
