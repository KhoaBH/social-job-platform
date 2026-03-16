package  vn.edu.uit.socialjob.platform.modules.network.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.edu.uit.socialjob.platform.modules.network.dto.FriendRequest;
import vn.edu.uit.socialjob.platform.modules.network.entity.Connection;
import vn.edu.uit.socialjob.platform.modules.network.repository.ConnectionRepository;
import vn.edu.uit.socialjob.platform.modules.user.repository.UserRepository;


@Service
public class ConnectionService {
    @Autowired
    private ConnectionRepository connectionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FollowService followService;
    public  Connection sendRequest(UUID requesterId,FriendRequest data) {
        Connection connection = new Connection();
        UUID addresseeId = data.getAddresseeId();
        if(requesterId.equals(addresseeId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }
        boolean alreadyExists = connectionRepository.existsByRequesterIdAndAddresseeId(requesterId, addresseeId);
        if(alreadyExists) {
            throw new IllegalArgumentException("Friend request already sent");
        }
        connection.setRequester(userRepository.getReferenceById(requesterId));
        connection.setAddressee(userRepository.getReferenceById(addresseeId));
        followService.follow(requesterId, addresseeId);
        connection.setStatus(vn.edu.uit.socialjob.platform.common.enums.ConnectionStatus.PENDING);
        return connectionRepository.save(connection);
    }

    public Connection acceptRequest(UUID connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new IllegalArgumentException("Connection request not found"));
        if (connection.getStatus() != vn.edu.uit.socialjob.platform.common.enums.ConnectionStatus.PENDING) {
            throw new IllegalStateException("Connection request is not pending");
        }
        connection.setStatus(vn.edu.uit.socialjob.platform.common.enums.ConnectionStatus.ACCEPTED);
        followService.follow(connection.getAddressee().getId(), connection.getRequester().getId());
        return connectionRepository.save(connection);
    }
    public List<Connection> getAll() {
        return connectionRepository.findAll();
    }

    public List<Connection> getConnectionsForUser(UUID userId) {
        return connectionRepository.findAllByUserId(userId);
    }
}