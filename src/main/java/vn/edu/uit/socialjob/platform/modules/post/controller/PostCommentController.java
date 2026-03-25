package vn.edu.uit.socialjob.platform.modules.post.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody PostCommentRequest data
    ) {
        return ResponseEntity.ok(postCommentService.create(postId, userId, data));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PostComment> update(
            @PathVariable UUID id,
            @Valid @RequestBody PostCommentRequest data
    ) {
        return ResponseEntity.ok(postCommentService.update(id, data));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        postCommentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
