package vn.edu.uit.socialjob.platform.modules.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.ToString;
import vn.edu.uit.socialjob.platform.modules.user.User;

@Entity
@Table(name = "user_auths")
@Data
public class UserAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

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
