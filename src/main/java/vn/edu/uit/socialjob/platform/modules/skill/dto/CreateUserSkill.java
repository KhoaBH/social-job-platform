package vn.edu.uit.socialjob.platform.modules.skill.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserSkill {
    private UUID skillId;
    private int level;
}
