package vn.edu.uit.socialjob.platform.modules.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import vn.edu.uit.socialjob.platform.modules.network.dto.FriendRequest;
import vn.edu.uit.socialjob.platform.modules.network.entity.Connection;
import vn.edu.uit.socialjob.platform.modules.network.service.ConnectionService;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @GetMapping
    public ResponseEntity<List<Connection>> listAll() {
        return ResponseEntity.ok(connectionService.getAll());
    }

    @GetMapping("/me")
    public ResponseEntity<List<Connection>> getMyConnections(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        UUID userId = UUID.fromString(authentication.getName());
        List<Connection> connections = connectionService.getConnectionsForUser(userId);
        return ResponseEntity.ok(connections);
    }

    @PostMapping("/request")
    public ResponseEntity<Connection> createRequest(
            @Valid @RequestBody FriendRequest request, 
            Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        UUID requesterId = UUID.fromString(authentication.getName()); 
        Connection newConnection = connectionService.sendRequest(requesterId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newConnection);
    }
    @PostMapping("/{connectionId}/accept")
    public ResponseEntity<Connection> acceptRequest(
            @PathVariable UUID connectionId, 
            Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Connection updatedConnection = connectionService.acceptRequest(connectionId);
        return ResponseEntity.ok(updatedConnection);
    }
}