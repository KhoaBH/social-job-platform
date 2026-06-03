package vn.edu.uit.socialjob.platform.modules.post.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.uit.socialjob.platform.modules.post.entity.PostInteraction;

public interface PostInteractionRepository extends JpaRepository<PostInteraction, UUID> {
    
    @Query("SELECT pi FROM PostInteraction pi WHERE pi.isDeleted = false")
    List<PostInteraction> findAll();
    
    @Query("SELECT pi FROM PostInteraction pi WHERE pi.id = :id AND pi.isDeleted = false")
    Optional<PostInteraction> findById(@Param("id") UUID id);
    
    @Query("SELECT pi FROM PostInteraction pi WHERE pi.postId = :postId AND pi.isDeleted = false")
    List<PostInteraction> findByPostId(@Param("postId") UUID postId);
    
    @Query("SELECT pi FROM PostInteraction pi WHERE pi.postId = :postId AND pi.userId = :userId AND pi.isDeleted = false")
    Optional<PostInteraction> findByPostIdAndUserId(@Param("postId") UUID postId, @Param("userId") UUID userId);

    @Query("SELECT pi FROM PostInteraction pi WHERE pi.postId = :postId AND pi.userId = :userId")
    Optional<PostInteraction> findAnyByPostIdAndUserId(@Param("postId") UUID postId, @Param("userId") UUID userId);

    @Query("""
        SELECT pi.postId AS postId, COUNT(pi) AS count
        FROM PostInteraction pi
        WHERE pi.isDeleted = false
          AND pi.postId IN :postIds
        GROUP BY pi.postId
        """)
    List<PostMetricCountProjection> countActiveByPostIds(@Param("postIds") Collection<UUID> postIds);

    @Query("""
        SELECT pi.postId AS postId, COUNT(pi) AS count
        FROM PostInteraction pi
        WHERE pi.isDeleted = false
          AND pi.postId IN :postIds
          AND pi.createdAt >= :fromTime
          AND pi.createdAt < :toTime
        GROUP BY pi.postId
        """)
    List<PostMetricCountProjection> countActiveByPostIdsBetween(
        @Param("postIds") Collection<UUID> postIds,
        @Param("fromTime") LocalDateTime fromTime,
        @Param("toTime") LocalDateTime toTime
    );
}
