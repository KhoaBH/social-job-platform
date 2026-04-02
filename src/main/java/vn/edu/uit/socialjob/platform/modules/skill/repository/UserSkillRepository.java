package vn.edu.uit.socialjob.platform.modules.skill.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.edu.uit.socialjob.platform.modules.skill.entity.UserSkill;

public interface UserSkillRepository extends JpaRepository<UserSkill, UUID> {
    @Query("SELECT us FROM UserSkill us WHERE us.user.id = :userId AND us.isDeleted = false")
    List<UserSkill> findByUserId(UUID userId);
}
