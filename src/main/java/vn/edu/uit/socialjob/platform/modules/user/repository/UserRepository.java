package  vn.edu.uit.socialjob.platform.modules.user.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.uit.socialjob.platform.modules.user.entity.User;

public interface UserRepository extends JpaRepository<User, java.util.UUID> {
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isDeleted = false")
    java.util.Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.isDeleted = false")
    java.util.Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false")
    List<User> findAll();

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.isDeleted = false")
    java.util.Optional<User> findById(@Param("id") java.util.UUID id);
}