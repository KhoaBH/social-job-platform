package vn.edu.uit.socialjob.platform.modules.post.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.socialjob.platform.modules.post.dto.FeedRequest;
import vn.edu.uit.socialjob.platform.modules.post.entity.Feed;
import vn.edu.uit.socialjob.platform.modules.post.service.FeedService;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feeds")
public class FeedController {
    
    @Autowired
    private FeedService feedService;
    
    @GetMapping
    public ResponseEntity<List<Feed>> listAll() {
        return ResponseEntity.ok(feedService.getAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Feed> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(feedService.getById(id));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Feed>> getByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(feedService.getByUserId(userId));
    }
    
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Feed>> getUnreadFeed(@PathVariable UUID userId) {
        return ResponseEntity.ok(feedService.getUnreadFeedByUserId(userId));
    }
    
    @PostMapping("/user/{userId}/post/{postId}")
    public ResponseEntity<Feed> create(
            @PathVariable UUID userId,
            @PathVariable UUID postId,
            @Valid @RequestBody FeedRequest data
    ) {
        return ResponseEntity.ok(feedService.create(userId, postId, data));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Feed> update(
            @PathVariable UUID id,
            @Valid @RequestBody FeedRequest data
    ) {
        return ResponseEntity.ok(feedService.update(id, data));
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<Feed> markAsRead(@PathVariable UUID id) {
        return ResponseEntity.ok(feedService.markAsRead(id));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        feedService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
