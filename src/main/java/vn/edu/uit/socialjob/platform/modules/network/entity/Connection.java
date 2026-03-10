package  vn.edu.uit.socialjob.platform.modules.network.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.edu.uit.socialjob.platform.common.entity.BaseEntity;
import vn.edu.uit.socialjob.platform.common.enums.ConnectionStatus;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;

@Entity
@Table(
    name = "connections",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_connections_requester_addressee", columnNames = {"requester_id", "addressee_id"})
    }
)
@Getter
@Setter
public class Connection extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    @ToString.Exclude
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "addressee_id", nullable = false)
    @ToString.Exclude
    private User addressee;

    @Column(nullable = false, length = 20)
    private ConnectionStatus status; // PENDING, ACCEPTED, REJECTED, BLOCKED

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Connection)) return false;
        Connection that = (Connection) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}