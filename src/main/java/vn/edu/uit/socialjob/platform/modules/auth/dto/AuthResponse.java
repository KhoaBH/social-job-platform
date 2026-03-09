package vn.edu.uit.socialjob.platform.modules.auth.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private UserDto user;

    @Data
    @Builder 
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDto {
        private UUID id;
        private String email;
        private String fullName;
        private String avatarUrl;
    }
}
