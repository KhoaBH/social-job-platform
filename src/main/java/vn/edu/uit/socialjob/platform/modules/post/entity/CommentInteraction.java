package vn.edu.uit.socialjob.platform.modules.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import vn.edu.uit.socialjob.platform.common.entity.BaseEntity;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
    name = "comment_interactions",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_comment_interactions_comment_user", columnNames = {"comment_id", "user_id"})
    }
)
public class CommentInteraction extends BaseEntity {

    @Column(name = "comment_id", nullable = false)
    private UUID commentId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
}