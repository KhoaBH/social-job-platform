package vn.edu.uit.socialjob.platform.modules.post.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.socialjob.platform.modules.post.dto.PostRequest;
import vn.edu.uit.socialjob.platform.modules.post.entity.Post;
import vn.edu.uit.socialjob.platform.modules.post.service.PostService;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    @GetMapping
    public ResponseEntity<List<Post>> listAll() {
        return ResponseEntity.ok(postService.getAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Post> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(postService.getById(id));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(postService.getByUserId(userId));
    }
    
    @PostMapping
    public ResponseEntity<Post> create(
           
            @Valid @RequestBody PostRequest data,Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(postService.create(UUID.fromString(authentication.getName()), data));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Post> update(
            @PathVariable UUID id,
            @Valid @RequestBody PostRequest data,Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(postService.update(id, data));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
