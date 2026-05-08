package vn.edu.uit.socialjob.platform.modules.network.dto;
import java.util.UUID;


import lombok.Data;

@Data
public class FriendRequestResponse {
    
    private UUID senderId;
    private String senderName;
    private String senderAvatarUrl;
    private UUID connectionId;
    private String status;
    private int mutualFriendsCount;
}
