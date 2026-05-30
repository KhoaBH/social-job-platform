package vn.edu.uit.socialjob.platform.modules.apply.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.uit.socialjob.platform.modules.apply.entity.Apply;

public interface ApplyRepository extends JpaRepository<Apply, UUID> {

  interface JobPostApplyCountView {
    UUID getJobPostId();
    Long getTotal();
  }

    @Query("""
        SELECT a
        FROM Apply a
        WHERE a.jobPost.id = :jobPostId
          AND a.isDeleted = false
    """)
    List<Apply> findAllActiveByJobPostId(@Param("jobPostId") UUID jobPostId);

    @Query("""
    SELECT a 
    FROM Apply a 
    JOIN FETCH a.jobPost jp 
    JOIN FETCH jp.company 
    WHERE a.user.id = :userId 
      AND a.isDeleted = false
""")
    List<Apply> findAllActiveByUserId(@Param("userId") UUID userId);

    @Query("""
        SELECT a
        FROM Apply a
        WHERE a.jobPost.id = :jobPostId
          AND a.user.id = :userId
          AND a.isDeleted = false
    """)
    Optional<Apply> findActiveByJobPostIdAndUserId(@Param("jobPostId") UUID jobPostId, @Param("userId") UUID userId);

    @Query("""
        SELECT a.jobPost.id as jobPostId, COUNT(a) as total
        FROM Apply a
        WHERE a.jobPost.id IN :jobPostIds
          AND a.isDeleted = false
        GROUP BY a.jobPost.id
    """)
    List<JobPostApplyCountView> countActiveByJobPostIds(@Param("jobPostIds") List<UUID> jobPostIds);
}
