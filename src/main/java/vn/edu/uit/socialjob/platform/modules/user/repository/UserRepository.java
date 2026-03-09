package  vn.edu.uit.socialjob.platform.modules.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.uit.socialjob.platform.modules.user.entity.User;

public interface UserRepository extends JpaRepository<User, java.util.UUID> {
    java.util.Optional<User> findByEmail(String email);
    java.util.Optional<User> findByUsername(String username);
}