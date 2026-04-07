package vn.edu.uit.socialjob.platform.modules.network.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;

@Data
@AllArgsConstructor
public class SuggestedUserDTO {
    private User user;
    private double score;
    private int mutual;
}
