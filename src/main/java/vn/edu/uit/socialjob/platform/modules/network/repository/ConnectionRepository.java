package  vn.edu.uit.socialjob.platform.modules.network.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.uit.socialjob.platform.common.enums.ConnectionStatus;
import vn.edu.uit.socialjob.platform.modules.network.entity.Connection;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;

public interface ConnectionRepository extends JpaRepository<Connection, UUID> {
    boolean existsByRequesterIdAndAddresseeId(UUID requesterId, UUID addresseeId);
    
    @Query("SELECT c FROM Connection c WHERE c.requester.id = :userId OR c.addressee.id = :userId ORDER BY c.status DESC")
    List<Connection> findAllByUserId(@Param("userId") UUID userId);
    
    List<Connection> findByRequesterIdOrAddresseeId(UUID requesterId, UUID addresseeId);

    List<Connection> findAllByAddresseeIdAndStatus(UUID addresseeId, ConnectionStatus status);
    @Query(value = """
    SELECT user_id
    FROM (
        SELECT requester_id AS user_id 
        FROM connections 
        WHERE status = 1

        UNION ALL

        SELECT addressee_id AS user_id 
        FROM connections 
        WHERE status = 1
    ) t
    GROUP BY user_id
    ORDER BY COUNT(*) DESC
    LIMIT 10
""", nativeQuery = true)
    List<UUID> findTop10PopularUsers();
    @Query(value = """
    SELECT u.id FROM users u
    WHERE u.id != :userId
    AND u.id NOT IN (
        SELECT c.requester_id FROM connections c 
        WHERE c.requester_id = :userId OR c.addressee_id = :userId
        UNION
        SELECT c.addressee_id FROM connections c 
        WHERE c.requester_id = :userId OR c.addressee_id = :userId
    )
    ORDER BY RANDOM()
    LIMIT :limit
    """, nativeQuery = true)
    List<UUID> findRandomUnconnectedUsers(@Param("userId") UUID userId, @Param("limit") int limit);

    @Query("""
        SELECT c FROM Connection c
        JOIN FETCH c.requester
        JOIN FETCH c.addressee
        WHERE c.status = :status
          AND (c.requester.id = :userId OR c.addressee.id = :userId)
        """)
    List<Connection> findAllAcceptedConnectionsOf(
        @Param("userId") UUID userId,
        @Param("status") ConnectionStatus status
    );

    // Lấy bạn bè của NHIỀU user cùng lúc (dùng để tính mutual, gom 1 query duy nhất)
    @Query("""
        SELECT c FROM Connection c
        WHERE c.status = :status
          AND (c.requester.id IN :userIds OR c.addressee.id IN :userIds)
        """)
    List<Connection> findAllAcceptedConnectionsOfUsers(
        @Param("userIds") Collection<UUID> userIds,
        @Param("status") ConnectionStatus status
    );
}