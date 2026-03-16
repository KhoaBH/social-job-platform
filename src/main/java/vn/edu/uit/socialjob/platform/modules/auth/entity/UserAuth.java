package vn.edu.uit.socialjob.platform.modules.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.edu.uit.socialjob.platform.common.entity.BaseEntity;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;

@Getter
@Setter
@Entity
@Table(name = "user_auths")
public class UserAuth extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    private String provider;

    @Column(name = "firebase_uid")
    private String firebaseUid;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "linked_at")
    private LocalDateTime linkedAt;
}
