package vn.edu.uit.socialjob.platform.modules.jobpost.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSkillRequest {

    @NotNull(message = "Skill is required")
    private UUID skillId;

    private Boolean required;
}