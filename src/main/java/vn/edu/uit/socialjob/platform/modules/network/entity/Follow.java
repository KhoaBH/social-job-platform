package vn.edu.uit.socialjob.platform.modules.network.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "follower_id", nullable = false)
	@ToString.Exclude
	private User follower;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "followee_id", nullable = false)
	@ToString.Exclude
	private User followee;

}
