package vn.edu.uit.socialjob.platform.modules.auth.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.uit.socialjob.platform.modules.auth.entity.UserAuth;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;

public interface UserAuthRepository extends JpaRepository<UserAuth, UUID> {
    Optional<UserAuth> findByProviderAndFirebaseUid(String provider, String firebaseUid);
    Optional<UserAuth> findByUserAndIsAdmin(User user, boolean isAdmin);
}
