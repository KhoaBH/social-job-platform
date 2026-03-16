package  vn.edu.uit.socialjob.platform.modules.skill.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillCategoryRequest {
    private String name;
    private String description;
}