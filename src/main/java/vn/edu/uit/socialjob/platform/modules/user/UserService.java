package  vn.edu.uit.socialjob.platform.modules.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }
        return userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }
}