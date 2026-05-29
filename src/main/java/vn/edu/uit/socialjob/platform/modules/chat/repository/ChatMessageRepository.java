package vn.edu.uit.socialjob.platform.modules.chat.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.uit.socialjob.platform.modules.chat.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    @Query("""
        SELECT cm
        FROM ChatMessage cm
        WHERE cm.isDeleted = false
          AND ((cm.sender.id = :userId AND cm.recipient.id = :otherUserId)
            OR (cm.sender.id = :otherUserId AND cm.recipient.id = :userId))
        ORDER BY cm.createdAt ASC
    """)
    List<ChatMessage> findConversation(@Param("userId") UUID userId, @Param("otherUserId") UUID otherUserId);
}
