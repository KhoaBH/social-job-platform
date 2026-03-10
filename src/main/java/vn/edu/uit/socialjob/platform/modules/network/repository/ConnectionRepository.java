package  vn.edu.uit.socialjob.platform.modules.network.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.uit.socialjob.platform.modules.network.entity.Connection;

public interface ConnectionRepository extends JpaRepository<Connection, UUID> {
    boolean existsByRequesterIdAndAddresseeId(UUID requesterId, UUID addresseeId);
    
    @Query("SELECT c FROM Connection c WHERE c.requester.id = :userId OR c.addressee.id = :userId")
    List<Connection> findAllByUserId(@Param("userId") UUID userId);
    
    List<Connection> findByRequesterIdOrAddresseeId(UUID requesterId, UUID addresseeId);
}