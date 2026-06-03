package vn.edu.uit.socialjob.platform.modules.post.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.uit.socialjob.platform.modules.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, UUID> {
    
    @Query("SELECT p FROM Post p WHERE p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Post> findAll();
    
    @Query("SELECT p FROM Post p WHERE p.id = :id AND p.isDeleted = false")
    Optional<Post> findById(@Param("id") UUID id);
    
    @Query("SELECT p FROM Post p WHERE p.author.id = :authorId AND p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Post> findByAuthorId(@Param("authorId") UUID authorId);

    @Query("""
        SELECT p
        FROM Post p
        JOIN FETCH p.author a
        WHERE p.isDeleted = false
          AND a.id IN :authorIds
          AND p.createdAt >= :createdAfter
        ORDER BY p.createdAt DESC
        """)
    List<Post> findRecentByAuthorIds(
        @Param("authorIds") Collection<UUID> authorIds,
        @Param("createdAfter") LocalDateTime createdAfter
    );
}
