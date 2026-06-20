package vn.edu.uit.socialjob.platform.modules.network.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import vn.edu.uit.socialjob.platform.modules.network.dto.MyFriendDTO;
import vn.edu.uit.socialjob.platform.modules.network.service.ConnectionService;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/friends")
@SecurityRequirement(name = "bearerAuth")
public class FriendController {
    @Autowired
    private ConnectionService connectionService;
    @GetMapping("")
    public List<MyFriendDTO> getFriendList(@RequestParam UUID userId) {
        return connectionService.getMyFriendsWithMutualCount(userId);
    }
    
}
