package vn.edu.uit.socialjob.platform.modules.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.edu.uit.socialjob.platform.common.entity.BaseEntity;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "post_interactions")
public class PostInteraction extends BaseEntity {
    
    @Column(nullable = false)
    private UUID postId;
    
    @Column(nullable = false)
    private UUID userId;
    
    @Column(nullable = false)
    private String interactionType; // e.g., "like", "love", "haha", etc.
}
