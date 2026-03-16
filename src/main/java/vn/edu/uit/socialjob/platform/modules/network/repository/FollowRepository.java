package vn.edu.uit.socialjob.platform.modules.network.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.uit.socialjob.platform.modules.network.entity.Follow;

public interface FollowRepository extends JpaRepository<Follow, UUID> {
	boolean existsByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);

	Optional<Follow> findByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);

	List<Follow> findByFollowerId(UUID followerId);

	List<Follow> findByFolloweeId(UUID followeeId);

	List<Follow> findByFollowerIdOrFolloweeId(UUID followerId, UUID followeeId);
}
