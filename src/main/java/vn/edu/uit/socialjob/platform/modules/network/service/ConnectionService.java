package  vn.edu.uit.socialjob.platform.modules.network.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.edu.uit.socialjob.platform.common.enums.ConnectionStatus;
import vn.edu.uit.socialjob.platform.modules.network.dto.MyFriendDTO;
import vn.edu.uit.socialjob.platform.modules.network.dto.FriendRequest;
import vn.edu.uit.socialjob.platform.modules.network.dto.FriendRequestResponse;
import vn.edu.uit.socialjob.platform.modules.network.entity.Connection;
import vn.edu.uit.socialjob.platform.modules.network.repository.ConnectionRepository;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
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
        User requester = userRepository.findById(requesterId)
            .orElseThrow(() -> new IllegalArgumentException("Requester not found"));
        User addressee = userRepository.findById(addresseeId)
            .orElseThrow(() -> new IllegalArgumentException("Addressee not found"));

        connection.setRequester(requester);
        connection.setAddressee(addressee);
        followService.follow(requesterId, addresseeId);
        connection.setStatus(vn.edu.uit.socialjob.platform.common.enums.ConnectionStatus.PENDING);
        return connectionRepository.save(connection);
    }
    public List<MyFriendDTO> getMyFriendsWithMutualCount(UUID myId) {
        // Query 1: bạn bè của chính tôi
        List<Connection> myConnections =
            connectionRepository.findAllAcceptedConnectionsOf(myId, ConnectionStatus.ACCEPTED);

        if (myConnections.isEmpty()) return List.of();

        // friendId của từng connection (người không phải là tôi)
        Map<UUID, Connection> friendIdToConnection = new LinkedHashMap<>();
        for (Connection c : myConnections) {
            UUID friendId = resolveFriendId(c, myId);
            friendIdToConnection.put(friendId, c);
        }
        Set<UUID> myFriendIds = friendIdToConnection.keySet();

        // Query 2: bạn bè CỦA TẤT CẢ bạn bè tôi, lấy 1 lần duy nhất
        List<Connection> friendsOfFriendsConnections =
            connectionRepository.findAllAcceptedConnectionsOfUsers(myFriendIds, ConnectionStatus.ACCEPTED);

        // Gom thành map: friendId -> Set<id bạn của friend đó>
        Map<UUID, Set<UUID>> friendOfFriendMap = new HashMap<>();
        for (Connection c : friendsOfFriendsConnections) {
            UUID a = c.getRequester().getId();
            UUID b = c.getAddressee().getId();
            // chỉ quan tâm cạnh có 1 đầu là bạn của tôi
            if (myFriendIds.contains(a)) {
                friendOfFriendMap.computeIfAbsent(a, k -> new HashSet<>()).add(b);
            }
            if (myFriendIds.contains(b)) {
                friendOfFriendMap.computeIfAbsent(b, k -> new HashSet<>()).add(a);
            }
        }

        // Build kết quả: với mỗi bạn của tôi, đếm giao giữa (bạn của họ) và (bạn của tôi)
        List<MyFriendDTO> result = new ArrayList<>();
        for (Map.Entry<UUID, Connection> entry : friendIdToConnection.entrySet()) {
            UUID friendId = entry.getKey();
            Connection c = entry.getValue();
            User friend = resolveFriend(c, myId);

            Set<UUID> theirFriends = friendOfFriendMap.getOrDefault(friendId, Set.of());
            long mutualCount = theirFriends.stream()
                .filter(id -> myFriendIds.contains(id) && !id.equals(myId))
                .count();

            result.add(new MyFriendDTO(
                c.getId(),
                friend.getId(),
                friend.getFullName(),
                friend.getHeadline(),
                mutualCount
            ));
        }

        return result;
    }

    private UUID resolveFriendId(Connection c, UUID myId) {
        return c.getRequester().getId().equals(myId)
            ? c.getAddressee().getId()
            : c.getRequester().getId();
    }

    private User resolveFriend(Connection c, UUID myId) {
        return c.getRequester().getId().equals(myId)
            ? c.getAddressee()
            : c.getRequester();
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
    public Connection rejectRequest(UUID connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new IllegalArgumentException("Connection request not found"));
        if (connection.getStatus() != vn.edu.uit.socialjob.platform.common.enums.ConnectionStatus.PENDING) {
            throw new IllegalStateException("Connection request is not pending");
        }
        connection.setStatus(vn.edu.uit.socialjob.platform.common.enums.ConnectionStatus.REJECTED);
        return connectionRepository.save(connection);
    }
    public List<Connection> getAll() {
        return connectionRepository.findAll();
    }

    public List<Connection> getConnectionsForUser(UUID userId) {
        return connectionRepository.findAllByUserId(userId);
    }

    public int mutualFriendsCount(UUID userId1, UUID userId2) {
        Set<UUID> friendsOfUser1 = getFriendIds(userId1);
        Set<UUID> friendsOfUser2 = getFriendIds(userId2);
        friendsOfUser1.retainAll(friendsOfUser2);
        return friendsOfUser1.size();
    }
    public Set<UUID> getFriendIds(UUID userId) {
        return connectionRepository.findAllByUserId(userId)
                .stream()
                .filter(c -> c.getStatus() == ConnectionStatus.ACCEPTED)
                .map(c -> {
                    if (c.getRequester().getId().equals(userId)) {
                        return c.getAddressee().getId();
                    } else {
                        return c.getRequester().getId();
                    }
                })
                .collect(Collectors.toSet());
    }


    public List<FriendRequestResponse> getRequestsForUser(UUID userId) {
        List<Connection> request =  connectionRepository.findAllByUserId(userId);
        return request.stream().map(c -> {
            FriendRequestResponse response = new FriendRequestResponse();
            response.setConnectionId(c.getId());
            response.setSenderId(c.getRequester().getId());
            response.setSenderName(c.getRequester().getFullName());
            response.setSenderAvatarUrl(c.getRequester().getAvatarUrl());
            response.setStatus(c.getStatus().name());
            response.setMutualFriendsCount(mutualFriendsCount(userId, c.getRequester().getId()));
            return response;
        }).toList();
    }
    public Set<UUID> getPopularUsers() {
        return new HashSet<>(connectionRepository.findTop10PopularUsers());
    }
}