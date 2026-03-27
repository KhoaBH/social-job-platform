package vn.edu.uit.socialjob.platform.modules.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.uit.socialjob.platform.modules.post.entity.PostComment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostCommentRepository extends JpaRepository<PostComment, UUID> {
    
    @Query("SELECT pc FROM PostComment pc WHERE pc.isDeleted = false")
    List<PostComment> findAll();
    
    @Query("SELECT pc FROM PostComment pc WHERE pc.id = :id AND pc.isDeleted = false")
    Optional<PostComment> findById(@Param("id") UUID id);
    
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId AND pc.isDeleted = false")
    List<PostComment> findByPostId(@Param("postId") UUID postId);
    
    @Query("SELECT pc FROM PostComment pc WHERE pc.parentComment.id = :parentCommentId AND pc.isDeleted = false")
    List<PostComment> findRepliesByCommentId(@Param("parentCommentId") UUID parentCommentId);
}
