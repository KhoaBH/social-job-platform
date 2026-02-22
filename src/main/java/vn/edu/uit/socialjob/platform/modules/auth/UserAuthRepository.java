package vn.edu.uit.socialjob.platform.modules.auth;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthRepository extends JpaRepository<UserAuth, UUID> {
    Optional<UserAuth> findByProviderAndFirebaseUid(String provider, String firebaseUid);
}
