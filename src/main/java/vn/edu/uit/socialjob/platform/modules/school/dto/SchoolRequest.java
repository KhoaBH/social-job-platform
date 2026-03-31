package vn.edu.uit.socialjob.platform.modules.school.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchoolRequest {
    @NotBlank(message = "School name is required")
    private String name;

    private String logoUrl;

    private String website;
}
