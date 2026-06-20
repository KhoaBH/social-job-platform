package vn.edu.uit.socialjob.platform.modules.network.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Getter
@Setter
@AllArgsConstructor
public class MyFriendDTO {
    private UUID connectionId;
    private UUID friendId;
    private String friendFullName;
    private String friendHeadline;
    private long mutualFriendsCount;
    
}
