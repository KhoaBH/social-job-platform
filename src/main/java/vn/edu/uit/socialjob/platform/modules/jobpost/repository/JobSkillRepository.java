package vn.edu.uit.socialjob.platform.modules.jobpost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobSkill;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobSkillRepository extends JpaRepository<JobSkill, UUID> {

    @Query("SELECT js FROM JobSkill js WHERE js.jobPost.id = :jobPostId AND js.isDeleted = false")
    List<JobSkill> findByJobPostId(@Param("jobPostId") UUID jobPostId);

    @Query("SELECT js FROM JobSkill js WHERE js.jobPost.id = :jobPostId AND js.skill.id = :skillId AND js.isDeleted = false")
    Optional<JobSkill> findByJobPostIdAndSkillId(@Param("jobPostId") UUID jobPostId, @Param("skillId") UUID skillId);

    @Query("SELECT js FROM JobSkill js WHERE js.id = :id AND js.isDeleted = false")
    Optional<JobSkill> findById(@Param("id") UUID id);
}