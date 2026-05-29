package vn.edu.uit.socialjob.platform.modules.chat.service;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.chat.dto.ChatMessageRequest;
import vn.edu.uit.socialjob.platform.modules.chat.dto.ChatMessageResponse;
import vn.edu.uit.socialjob.platform.modules.chat.entity.ChatMessage;
import vn.edu.uit.socialjob.platform.modules.chat.repository.ChatMessageRepository;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.repository.UserRepository;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(
        ChatMessageRepository chatMessageRepository,
        UserRepository userRepository,
        SimpMessagingTemplate messagingTemplate
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public List<ChatMessageResponse> getConversation(UUID userId, UUID otherUserId) {
        ensureUserExists(userId);
        ensureUserExists(otherUserId);

        return chatMessageRepository.findConversation(userId, otherUserId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public ChatMessageResponse sendMessage(UUID senderId, @Valid ChatMessageRequest request) {
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User recipient = userRepository.findById(request.getRecipientId())
            .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));

        if (sender.getId().equals(recipient.getId())) {
            throw new IllegalArgumentException("Cannot send message to yourself");
        }

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(request.getContent().trim());

        ChatMessage saved = chatMessageRepository.save(message);
        ChatMessageResponse response = toResponse(saved);

        messagingTemplate.convertAndSendToUser(recipient.getId().toString(), "/queue/messages", response);
        messagingTemplate.convertAndSendToUser(sender.getId().toString(), "/queue/messages", response);
        return response;
    }

    private void ensureUserExists(UUID userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
    }

    private ChatMessageResponse toResponse(ChatMessage message) {
        return ChatMessageResponse.builder()
            .id(message.getId())
            .senderId(message.getSender().getId())
            .recipientId(message.getRecipient().getId())
            .content(message.getContent())
            .createdAt(message.getCreatedAt())
            .build();
    }
}
