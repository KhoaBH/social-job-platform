package vn.edu.uit.socialjob.platform.modules.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.edu.uit.socialjob.platform.common.entity.BaseEntity;
import vn.edu.uit.socialjob.platform.common.enums.VisibilityStatus;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = true)
    private UUID companyId;
    
    @Column(columnDefinition = "text")
    private String content;
    
    @Column(name = "interaction_count")
    private VisibilityStatus visibility = VisibilityStatus.PUBLIC;
}
