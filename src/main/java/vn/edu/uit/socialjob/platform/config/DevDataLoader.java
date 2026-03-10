package vn.edu.uit.socialjob.platform.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.repository.UserRepository;

@Slf4j
@Configuration
public class DevDataLoader {

    @Bean
    CommandLineRunner initDevData(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() > 0) {
                log.info("Database already has users, skipping dev data initialization");
                return;
            }

            log.info("Initializing dev test users...");
            
            List<User> testUsers = List.of(
                createUser("alice@test.com", "alice", "Alice Nguyen", "Software Engineer"),
                createUser("bob@test.com", "bob", "Bob Tran", "Product Manager"),
                createUser("charlie@test.com", "charlie", "Charlie Le", "UI/UX Designer"),
                createUser("diana@test.com", "diana", "Diana Pham", "Data Analyst"),
                createUser("eve@test.com", "eve", "Eve Hoang", "Marketing Specialist")
            );

            userRepository.saveAll(testUsers);
            
            log.info("✅ Created {} test users for development", testUsers.size());
            log.info("📝 Use POST /api/auth/dev-login with any email to get JWT token");
            log.info("📝 Example: {{ \"email\": \"alice@test.com\" }}");
        };
    }

    private User createUser(String email, String username, String fullName, String headline) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setFullName(fullName);
        user.setHeadline(headline);
        user.setAvatarUrl("https://ui-avatars.com/api/?name=" + fullName.replace(" ", "+"));
        return user;
    }
}
