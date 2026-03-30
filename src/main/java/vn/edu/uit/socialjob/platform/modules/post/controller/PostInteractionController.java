package vn.edu.uit.socialjob.platform.modules.post.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import vn.edu.uit.socialjob.platform.modules.post.entity.PostInteraction;
import vn.edu.uit.socialjob.platform.modules.post.service.PostInteractionService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/post-interactions")
public class PostInteractionController {

	private final PostInteractionService postInteractionService;

	public PostInteractionController(PostInteractionService postInteractionService) {
		this.postInteractionService = postInteractionService;
	}

	@GetMapping
	public ResponseEntity<List<PostInteraction>> listAll() {
		return ResponseEntity.ok(postInteractionService.getAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<PostInteraction> getById(@PathVariable UUID id) {
		return ResponseEntity.ok(postInteractionService.getById(id));
	}

	@GetMapping("/post/{postId}")
	public ResponseEntity<List<PostInteraction>> getByPostId(@PathVariable UUID postId) {
		return ResponseEntity.ok(postInteractionService.getByPostId(postId));
	}

	@GetMapping("/post/{postId}/me")
	public ResponseEntity<Optional<PostInteraction>> getMyInteraction(
		@PathVariable UUID postId,
		Authentication authentication
	) {
		UUID userId = extractUserId(authentication);
		return ResponseEntity.ok(postInteractionService.getUserInteractionOnPost(postId, userId));
	}

	@PostMapping("/post/{postId}")
	public ResponseEntity<PostInteraction> like(
		@PathVariable UUID postId,
		Authentication authentication
	) {
		UUID userId = extractUserId(authentication);
		return ResponseEntity.ok(postInteractionService.like(postId, userId));
	}

	@DeleteMapping("/post/{postId}/me")
	public ResponseEntity<Void> unlikeByPost(
		@PathVariable UUID postId,
		Authentication authentication
	) {
		UUID userId = extractUserId(authentication);
		postInteractionService.unlikeByPost(postId, userId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
		UUID userId = extractUserId(authentication);
		postInteractionService.delete(id, userId);
		return ResponseEntity.noContent().build();
	}

	private UUID extractUserId(Authentication authentication) {
		if (authentication == null || authentication.getName() == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		}

		try {
			return UUID.fromString(authentication.getName());
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		}
	}
}
