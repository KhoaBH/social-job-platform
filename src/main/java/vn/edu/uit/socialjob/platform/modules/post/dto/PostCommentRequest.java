package vn.edu.uit.socialjob.platform.modules.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentRequest {
    
    @NotBlank(message = "Content is required")
    private String content;


    
    private UUID parentCommentId; // for nested replies

}
