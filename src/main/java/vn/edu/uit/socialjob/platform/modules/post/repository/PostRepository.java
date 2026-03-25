package vn.edu.uit.socialjob.platform.modules.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.uit.socialjob.platform.modules.post.entity.Post;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    
    @Query("SELECT p FROM Post p WHERE p.isDeleted = false")
    List<Post> findAll();
    
    @Query("SELECT p FROM Post p WHERE p.id = :id AND p.isDeleted = false")
    Optional<Post> findById(@Param("id") UUID id);
    
    @Query("SELECT p FROM Post p WHERE p.userId = :userId AND p.isDeleted = false")
    List<Post> findByUserId(@Param("userId") UUID userId);
}
