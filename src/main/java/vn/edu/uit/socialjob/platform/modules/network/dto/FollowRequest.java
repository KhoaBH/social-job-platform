package vn.edu.uit.socialjob.platform.modules.network.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FollowRequest {
    @NotNull
    private UUID followeeId;
}
