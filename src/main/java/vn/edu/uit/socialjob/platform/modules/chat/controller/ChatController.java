package vn.edu.uit.socialjob.platform.modules.chat.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import vn.edu.uit.socialjob.platform.modules.chat.dto.ChatMessageRequest;
import vn.edu.uit.socialjob.platform.modules.chat.dto.ChatMessageResponse;
import vn.edu.uit.socialjob.platform.modules.chat.service.ChatService;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{otherUserId}")
    public ResponseEntity<List<ChatMessageResponse>> getConversation(
        @PathVariable UUID otherUserId,
        Principal principal
    ) {
        UUID userId = extractUserId(principal);
        return ResponseEntity.ok(chatService.getConversation(userId, otherUserId));
    }

    @PostMapping
    public ResponseEntity<ChatMessageResponse> sendViaRest(
        @Valid @RequestBody ChatMessageRequest request,
        Principal principal
    ) {
        UUID userId = extractUserId(principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.sendMessage(userId, request));
    }

    @MessageMapping("/chat.send")
    public void sendViaWebSocket(@Valid @Payload ChatMessageRequest request, Principal principal) {
        UUID userId = extractUserId(principal);
        chatService.sendMessage(userId, request);
    }

    private UUID extractUserId(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        try {
            return UUID.fromString(principal.getName());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}
