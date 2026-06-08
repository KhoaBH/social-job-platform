package vn.edu.uit.socialjob.platform.modules.skill.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkillDictionary {
    private UUID id;
    private String name;
    private List<String> aliases;
}
