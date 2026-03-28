package vn.edu.uit.socialjob.platform.modules.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedRequest {
    
    @NotBlank(message = "Feed type is required")
    private String feedType;
}
