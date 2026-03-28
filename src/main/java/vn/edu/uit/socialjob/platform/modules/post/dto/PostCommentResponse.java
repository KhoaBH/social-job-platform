package vn.edu.uit.socialjob.platform.modules.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentResponse {
    private UUID id;
    private String content;
    private String username;
    private LocalDateTime createdAt;
    private List<PostCommentResponse> replies;
}
