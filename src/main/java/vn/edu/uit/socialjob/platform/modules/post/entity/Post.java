package vn.edu.uit.socialjob.platform.modules.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.edu.uit.socialjob.platform.common.entity.BaseEntity;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post extends BaseEntity {
    
    @Column(nullable = false)
    private UUID userId;
    
    @Column(nullable = false, columnDefinition = "text")
    private String title;
    
    @Column(columnDefinition = "text")
    private String description;
    
    @Column(columnDefinition = "text")
    private String content;
    
    @Column(name = "interaction_count")
    private Integer interactionCount = 0;
    
    @Column(name = "comment_count")
    private Integer commentCount = 0;
}
