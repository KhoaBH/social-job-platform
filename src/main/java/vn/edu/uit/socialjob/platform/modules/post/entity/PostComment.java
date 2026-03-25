package vn.edu.uit.socialjob.platform.modules.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.edu.uit.socialjob.platform.common.entity.BaseEntity;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "post_comments")
public class PostComment extends BaseEntity {
    
    @Column(nullable = false)
    private UUID postId;
    
    @Column(nullable = false)
    private UUID userId;
    
    @Column(columnDefinition = "text", nullable = false)
    private String content;
    
    @Column(name = "parent_comment_id")
    private UUID parentCommentId; // for nested replies
}
