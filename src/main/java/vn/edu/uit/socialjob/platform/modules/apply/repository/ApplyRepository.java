package vn.edu.uit.socialjob.platform.modules.apply.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.uit.socialjob.platform.modules.apply.entity.Apply;

public interface ApplyRepository extends JpaRepository<Apply, UUID> {

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
}
