package vn.edu.uit.socialjob.platform.modules.post.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.uit.socialjob.platform.modules.post.entity.PostComment;

public interface PostCommentRepository extends JpaRepository<PostComment, UUID> {
    
    @Query("SELECT pc FROM PostComment pc WHERE pc.isDeleted = false")
    List<PostComment> findAll();
    
    @Query("SELECT pc FROM PostComment pc WHERE pc.id = :id AND pc.isDeleted = false")
    Optional<PostComment> findById(@Param("id") UUID id);
    
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId AND pc.isDeleted = false")
    List<PostComment> findByPostId(@Param("postId") UUID postId);

    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId AND pc.parentComment IS NULL AND pc.isDeleted = false")
    List<PostComment> findRootByPostId(@Param("postId") UUID postId);

    @Query("""
        SELECT pc
        FROM PostComment pc
        JOIN FETCH pc.user
        LEFT JOIN FETCH pc.parentComment
        WHERE pc.post.id = :postId AND pc.isDeleted = false
        ORDER BY pc.createdAt ASC
        """)
    List<PostComment> findByPostIdWithUser(@Param("postId") UUID postId);
    
    @Query("SELECT pc FROM PostComment pc WHERE pc.parentComment.id = :parentCommentId AND pc.isDeleted = false")
    List<PostComment> findRepliesByCommentId(@Param("parentCommentId") UUID parentCommentId);

    @Query("""
        SELECT pc.post.id AS postId, COUNT(pc) AS count
        FROM PostComment pc
        WHERE pc.isDeleted = false
          AND pc.post.id IN :postIds
        GROUP BY pc.post.id
        """)
    List<PostMetricCountProjection> countActiveByPostIds(@Param("postIds") Collection<UUID> postIds);

    @Query("""
        SELECT pc.post.id AS postId, COUNT(pc) AS count
        FROM PostComment pc
        WHERE pc.isDeleted = false
          AND pc.post.id IN :postIds
          AND pc.createdAt >= :fromTime
          AND pc.createdAt < :toTime
        GROUP BY pc.post.id
        """)
    List<PostMetricCountProjection> countActiveByPostIdsBetween(
        @Param("postIds") Collection<UUID> postIds,
        @Param("fromTime") LocalDateTime fromTime,
        @Param("toTime") LocalDateTime toTime
    );
}
