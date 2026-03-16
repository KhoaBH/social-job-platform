package vn.edu.uit.socialjob.platform.modules.network.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import vn.edu.uit.socialjob.platform.modules.network.entity.Follow;
import vn.edu.uit.socialjob.platform.modules.network.repository.FollowRepository;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.repository.UserRepository;

@Service
public class FollowService {

	private final FollowRepository followRepository;
	private final UserRepository userRepository;

	public FollowService(FollowRepository followRepository, UserRepository userRepository) {
		this.followRepository = followRepository;
		this.userRepository = userRepository;
	}

	public Follow follow(UUID followerId, UUID followeeId) {
		if (followerId.equals(followeeId)) {
			throw new IllegalArgumentException("Cannot follow yourself");
		}

		if (followRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
			throw new IllegalStateException("Already followed this user");
		}

		User follower = userRepository.findById(followerId)
			.orElseThrow(() -> new IllegalArgumentException("Follower not found"));
		User followee = userRepository.findById(followeeId)
			.orElseThrow(() -> new IllegalArgumentException("Followee not found"));

		Follow follow = new Follow();
		follow.setFollower(follower);
		follow.setFollowee(followee);

		return followRepository.save(follow);
	}

	public void unfollow(UUID followerId, UUID followeeId) {
		Follow existing = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId)
			.orElseThrow(() -> new IllegalArgumentException("Follow relationship not found"));
		followRepository.delete(existing);
	}

	public List<Follow> getMyFollowing(UUID userId) {
		return followRepository.findByFollowerId(userId);
	}

	public List<Follow> getMyFollowers(UUID userId) {
		return followRepository.findByFolloweeId(userId);
	}

	public List<Follow> getMyFollowGraph(UUID userId) {
		return followRepository.findByFollowerIdOrFolloweeId(userId, userId);
	}
}
