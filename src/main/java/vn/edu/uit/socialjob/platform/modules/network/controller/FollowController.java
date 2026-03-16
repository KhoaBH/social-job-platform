package vn.edu.uit.socialjob.platform.modules.network.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import vn.edu.uit.socialjob.platform.modules.network.entity.Follow;
import vn.edu.uit.socialjob.platform.modules.network.service.FollowService;

@RestController
@RequestMapping("/api/follows")
@SecurityRequirement(name = "bearerAuth")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @Operation(summary = "Follow a user")
    @PostMapping("/{followeeId}")
    public ResponseEntity<Follow> follow(
        @PathVariable UUID followeeId,
        Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID followerId = UUID.fromString(authentication.getName());
        Follow follow = followService.follow(followerId, followeeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(follow);
    }

    @Operation(summary = "Unfollow a user")
    @DeleteMapping("/{followeeId}")
    public ResponseEntity<Void> unfollow(
        @PathVariable UUID followeeId,
        Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID followerId = UUID.fromString(authentication.getName());
        followService.unfollow(followerId, followeeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get users I am following")
    @GetMapping("/me/following")
    public ResponseEntity<List<Follow>> myFollowing(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(followService.getMyFollowing(userId));
    }

    @Operation(summary = "Get my followers")
    @GetMapping("/me/followers")
    public ResponseEntity<List<Follow>> myFollowers(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(followService.getMyFollowers(userId));
    }

    @Operation(summary = "Get all follow relationships related to me")
    @GetMapping("/me")
    public ResponseEntity<List<Follow>> myFollowGraph(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(followService.getMyFollowGraph(userId));
    }
}
