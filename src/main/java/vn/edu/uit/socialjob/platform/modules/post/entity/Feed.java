package vn.edu.uit.socialjob.platform.modules.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.edu.uit.socialjob.platform.common.entity.BaseEntity;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "feeds")
public class Feed extends BaseEntity {
    
    @Column(nullable = false)
    private UUID userId;
    
    @Column(nullable = false)
    private UUID postId;
    
    @Column(name = "feed_type")
    private String feedType; // e.g., "post", "shared_post", etc.
    
    @Column(name = "is_read")
    private Boolean isRead = false;
}
