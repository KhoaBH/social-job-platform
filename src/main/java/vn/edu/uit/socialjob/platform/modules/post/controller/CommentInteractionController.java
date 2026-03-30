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
import vn.edu.uit.socialjob.platform.modules.post.entity.CommentInteraction;
import vn.edu.uit.socialjob.platform.modules.post.service.CommentInteractionService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/comment-interactions")
public class CommentInteractionController {

    private final CommentInteractionService commentInteractionService;

    public CommentInteractionController(CommentInteractionService commentInteractionService) {
        this.commentInteractionService = commentInteractionService;
    }

    @GetMapping
    public ResponseEntity<List<CommentInteraction>> listAll() {
        return ResponseEntity.ok(commentInteractionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentInteraction> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(commentInteractionService.getById(id));
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<List<CommentInteraction>> getByCommentId(@PathVariable UUID commentId) {
        return ResponseEntity.ok(commentInteractionService.getByCommentId(commentId));
    }

    @GetMapping("/comment/{commentId}/me")
    public ResponseEntity<Optional<CommentInteraction>> getMyInteraction(
        @PathVariable UUID commentId,
        Authentication authentication
    ) {
        UUID userId = extractUserId(authentication);
        return ResponseEntity.ok(commentInteractionService.getUserInteractionOnComment(commentId, userId));
    }

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<CommentInteraction> like(
        @PathVariable UUID commentId,
        Authentication authentication
    ) {
        UUID userId = extractUserId(authentication);
        return ResponseEntity.ok(commentInteractionService.like(commentId, userId));
    }

    @DeleteMapping("/comment/{commentId}/me")
    public ResponseEntity<Void> unlikeByComment(
        @PathVariable UUID commentId,
        Authentication authentication
    ) {
        UUID userId = extractUserId(authentication);
        commentInteractionService.unlikeByComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        UUID userId = extractUserId(authentication);
        commentInteractionService.delete(id, userId);
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