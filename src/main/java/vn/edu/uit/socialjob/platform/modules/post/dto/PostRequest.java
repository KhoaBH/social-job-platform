package vn.edu.uit.socialjob.platform.modules.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.uit.socialjob.platform.common.enums.VisibilityStatus;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    
    private UUID companyId;
    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Visibility status is required")
    private VisibilityStatus visibility;
}
