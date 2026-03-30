package vn.edu.uit.socialjob.platform.modules.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.edu.uit.socialjob.platform.common.entity.BaseEntity;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
    name = "post_interactions",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_post_interactions_post_user", columnNames = {"post_id", "user_id"})
    }
)
public class PostInteraction extends BaseEntity {
    
    @Column(name = "post_id", nullable = false)
    private UUID postId;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
}
