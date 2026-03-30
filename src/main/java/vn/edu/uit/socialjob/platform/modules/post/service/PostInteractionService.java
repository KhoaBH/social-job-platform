package vn.edu.uit.socialjob.platform.modules.post.service;

import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.post.entity.PostInteraction;
import vn.edu.uit.socialjob.platform.modules.post.repository.PostInteractionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostInteractionService {

	private final PostInteractionRepository postInteractionRepository;
	private final PostService postService;

	public PostInteractionService(
		PostInteractionRepository postInteractionRepository,
		PostService postService
	) {
		this.postInteractionRepository = postInteractionRepository;
		this.postService = postService;
	}

	public List<PostInteraction> getAll() {
		return postInteractionRepository.findAll();
	}

	public PostInteraction getById(UUID id) {
		return postInteractionRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Post interaction not found"));
	}

	public List<PostInteraction> getByPostId(UUID postId) {
		return postInteractionRepository.findByPostId(postId);
	}

	public Optional<PostInteraction> getUserInteractionOnPost(UUID postId, UUID userId) {
		return postInteractionRepository.findByPostIdAndUserId(postId, userId);
	}

	public PostInteraction like(UUID postId, UUID userId) {
		postService.getById(postId);

		Optional<PostInteraction> existing = postInteractionRepository.findAnyByPostIdAndUserId(postId, userId);
		if (existing.isPresent()) {
			PostInteraction interaction = existing.get();
			if (interaction.isDeleted()) {
				interaction.setDeleted(false);
				return postInteractionRepository.save(interaction);
			}
			return interaction;
		}

		PostInteraction interaction = new PostInteraction();
		interaction.setPostId(postId);
		interaction.setUserId(userId);
		return postInteractionRepository.save(interaction);
	}

	public void unlikeByPost(UUID postId, UUID userId) {
		Optional<PostInteraction> existing = postInteractionRepository.findByPostIdAndUserId(postId, userId);
		if (existing.isEmpty()) {
			return;
		}

		PostInteraction interaction = existing.get();
		interaction.setDeleted(true);
		postInteractionRepository.save(interaction);
	}

	public void delete(UUID id, UUID userId) {
		PostInteraction interaction = getById(id);
		ensureOwner(interaction, userId);
		interaction.setDeleted(true);
		postInteractionRepository.save(interaction);
	}

	private void ensureOwner(PostInteraction interaction, UUID userId) {
		if (!interaction.getUserId().equals(userId)) {
			throw new IllegalStateException("You are not allowed to modify this interaction");
		}
	}
}
