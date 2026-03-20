package vn.edu.uit.socialjob.platform.modules.skill.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillAliasRequest {
    @NotBlank(message = "Alias is required")
    private String alias;

    @NotBlank(message = "Skill ID is required")
    private UUID skillId;
}
