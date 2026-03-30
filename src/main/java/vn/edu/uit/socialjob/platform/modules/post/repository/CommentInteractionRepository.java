package vn.edu.uit.socialjob.platform.modules.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.uit.socialjob.platform.modules.post.entity.CommentInteraction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentInteractionRepository extends JpaRepository<CommentInteraction, UUID> {

    @Query("SELECT ci FROM CommentInteraction ci WHERE ci.isDeleted = false")
    List<CommentInteraction> findAll();

    @Query("SELECT ci FROM CommentInteraction ci WHERE ci.id = :id AND ci.isDeleted = false")
    Optional<CommentInteraction> findById(@Param("id") UUID id);

    @Query("SELECT ci FROM CommentInteraction ci WHERE ci.commentId = :commentId AND ci.isDeleted = false")
    List<CommentInteraction> findByCommentId(@Param("commentId") UUID commentId);

    @Query("SELECT ci FROM CommentInteraction ci WHERE ci.commentId = :commentId AND ci.userId = :userId AND ci.isDeleted = false")
    Optional<CommentInteraction> findByCommentIdAndUserId(@Param("commentId") UUID commentId, @Param("userId") UUID userId);

    @Query("SELECT ci FROM CommentInteraction ci WHERE ci.commentId = :commentId AND ci.userId = :userId")
    Optional<CommentInteraction> findAnyByCommentIdAndUserId(@Param("commentId") UUID commentId, @Param("userId") UUID userId);
}