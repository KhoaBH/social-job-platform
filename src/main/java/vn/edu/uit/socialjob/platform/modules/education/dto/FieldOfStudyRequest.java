package vn.edu.uit.socialjob.platform.modules.education.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldOfStudyRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
}
