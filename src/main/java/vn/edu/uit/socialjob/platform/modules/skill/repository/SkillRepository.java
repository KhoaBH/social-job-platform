package vn.edu.uit.socialjob.platform.modules.skill.repository;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.uit.socialjob.platform.modules.skill.entity.Skill;

public interface SkillRepository extends JpaRepository<Skill, UUID> {
    Optional<Skill> findByName(String name);
}
