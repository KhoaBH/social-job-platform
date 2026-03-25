package vn.edu.uit.socialjob.platform.modules.post.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.socialjob.platform.modules.post.dto.PostInteractionRequest;
import vn.edu.uit.socialjob.platform.modules.post.entity.PostInteraction;
import vn.edu.uit.socialjob.platform.modules.post.service.PostInteractionService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/post-interactions")
public class PostInteractionController {
    
    @Autowired
    private PostInteractionService postInteractionService;
    
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
    
    @GetMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<Optional<PostInteraction>> getUserInteraction(
            @PathVariable UUID postId,
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(postInteractionService.getUserInteractionOnPost(postId, userId));
    }
    
    @PostMapping("/post/{postId}")
    public ResponseEntity<PostInteraction> create(
            @PathVariable UUID postId,
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody PostInteractionRequest data
    ) {
        return ResponseEntity.ok(postInteractionService.create(postId, userId, data));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PostInteraction> update(
            @PathVariable UUID id,
            @Valid @RequestBody PostInteractionRequest data
    ) {
        return ResponseEntity.ok(postInteractionService.update(id, data));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        postInteractionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
