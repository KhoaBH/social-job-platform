package vn.edu.uit.socialjob.platform.modules.skill.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkillAliasResponse {
    private UUID id;
    private String name;
    private String alias;
}
