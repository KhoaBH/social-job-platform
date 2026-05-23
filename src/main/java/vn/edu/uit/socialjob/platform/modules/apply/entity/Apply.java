package vn.edu.uit.socialjob.platform.modules.apply.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import vn.edu.uit.socialjob.platform.common.entity.BaseEntity;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobPost;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
    name = "applies",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_applies_job_post_user", columnNames = {"job_post_id", "user_id"})
    }
)
public class Apply extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_post_id", nullable = false)
    private JobPost jobPost;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "score")
    private Double score;

    @Column(name = "score_updated_at")
    private LocalDateTime scoreUpdatedAt;
}
