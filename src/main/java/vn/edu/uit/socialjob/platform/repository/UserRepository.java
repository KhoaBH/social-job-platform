package  vn.edu.uit.socialjob.platform.repository;

import vn.edu.uit.socialjob.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    java.util.Optional<User> findByEmail(String email);
}