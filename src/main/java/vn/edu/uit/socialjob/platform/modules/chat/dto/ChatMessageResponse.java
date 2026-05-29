package vn.edu.uit.socialjob.platform.modules.chat.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatMessageResponse {
    private UUID id;
    private UUID senderId;
    private UUID recipientId;
    private String content;
    private LocalDateTime createdAt;
}
