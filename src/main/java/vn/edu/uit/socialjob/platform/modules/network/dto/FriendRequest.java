package vn.edu.uit.socialjob.platform.modules.network.dto;
import java.util.UUID;

import com.google.firebase.internal.NonNull;

import lombok.Data;

@Data
public class FriendRequest {
    @NonNull
    private UUID addresseeId;
}
