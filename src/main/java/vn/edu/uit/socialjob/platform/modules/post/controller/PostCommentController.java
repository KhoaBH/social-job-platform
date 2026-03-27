package vn.edu.uit.socialjob.platform.modules.post.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import vn.edu.uit.socialjob.platform.modules.post.dto.PostCommentRequest;
import vn.edu.uit.socialjob.platform.modules.post.entity.PostComment;
import vn.edu.uit.socialjob.platform.modules.post.service.PostCommentService;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/post-comments")
public class PostCommentController {
    
    @Autowired
    private PostCommentService postCommentService;
    
    @GetMapping
    public ResponseEntity<List<PostComment>> listAll() {
        return ResponseEntity.ok(postCommentService.getAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PostComment> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(postCommentService.getById(id));
    }
    
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<PostComment>> getByPostId(@PathVariable UUID postId) {
        return ResponseEntity.ok(postCommentService.getByPostId(postId));
    }
    
    @GetMapping("/{id}/replies")
    public ResponseEntity<List<PostComment>> getReplies(@PathVariable UUID id) {
        return ResponseEntity.ok(postCommentService.getRepliesByCommentId(id));
    }
    
    @PostMapping("/post/{postId}")
    public ResponseEntity<PostComment> create(
            @PathVariable UUID postId,
            @Valid @RequestBody PostCommentRequest data,
            Authentication authentication
    ) {
        UUID userId = extractUserId(authentication);
        return ResponseEntity.ok(postCommentService.create(userId, postId, data));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PostComment> update(
            @PathVariable UUID id,
            @Valid @RequestBody PostCommentRequest data,
            Authentication authentication
    ) {
        UUID userId = extractUserId(authentication);
        return ResponseEntity.ok(postCommentService.update(id, userId, data));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        UUID userId = extractUserId(authentication);
        postCommentService.delete(id, userId);
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
