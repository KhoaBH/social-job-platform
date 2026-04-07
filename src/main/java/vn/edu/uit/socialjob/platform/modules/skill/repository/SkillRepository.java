package vn.edu.uit.socialjob.platform.modules.skill.repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.uit.socialjob.platform.modules.skill.entity.Skill;

public interface SkillRepository extends JpaRepository<Skill, UUID> {
    @Query("SELECT s FROM Skill s WHERE s.name = :name AND s.isDeleted = false")
    Optional<Skill> findByName(@Param("name") String name);

    @Query("SELECT s FROM Skill s WHERE s.isDeleted = false")
    List<Skill> findAll();

    @Query("SELECT s FROM Skill s WHERE s.id = :id AND s.isDeleted = false")
    Optional<Skill> findById(@Param("id") UUID id);

}
