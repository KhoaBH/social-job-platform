package vn.edu.uit.socialjob.platform.modules.network.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.edu.uit.socialjob.platform.common.entity.BaseEntity;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;

@Entity
@Table(
	name = "follows",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_follows_follower_followee", columnNames = {"follower_id", "followee_id"})
	}
)
@Getter
@Setter
public class Follow extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(columnDefinition = "uuid")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "follower_id", nullable = false)
	@ToString.Exclude
	private User follower;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "followee_id", nullable = false)
	@ToString.Exclude
	private User followee;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Follow)) return false;
		Follow that = (Follow) o;
		return id != null && id.equals(that.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
