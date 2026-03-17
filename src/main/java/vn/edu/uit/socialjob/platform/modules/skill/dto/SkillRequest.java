package vn.edu.uit.socialjob.platform.modules.skill.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private UUID categoryId;
}
