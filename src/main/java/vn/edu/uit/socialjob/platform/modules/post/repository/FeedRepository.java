package vn.edu.uit.socialjob.platform.modules.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.uit.socialjob.platform.modules.post.entity.Feed;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedRepository extends JpaRepository<Feed, UUID> {
    
    @Query("SELECT f FROM Feed f WHERE f.isDeleted = false")
    List<Feed> findAll();
    
    @Query("SELECT f FROM Feed f WHERE f.id = :id AND f.isDeleted = false")
    Optional<Feed> findById(@Param("id") UUID id);
    
    @Query("SELECT f FROM Feed f WHERE f.userId = :userId AND f.isDeleted = false")
    List<Feed> findByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT f FROM Feed f WHERE f.userId = :userId AND f.isRead = false AND f.isDeleted = false")
    List<Feed> findUnreadFeedByUserId(@Param("userId") UUID userId);
}
