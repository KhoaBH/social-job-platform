package  vn.edu.uit.socialjob.platform.modules.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }
        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username này đã được sử dụng!");
        }
        return userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(java.util.UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User update(java.util.UUID id, User userUpdate) {
        User user = this.getById(id);
        
        if (userUpdate.getEmail() != null && !userUpdate.getEmail().isEmpty()) {
            if (!user.getEmail().equals(userUpdate.getEmail()) && 
                userRepository.findByEmail(userUpdate.getEmail()).isPresent()) {
                throw new RuntimeException("Email này đã được sử dụng!");
            }
            user.setEmail(userUpdate.getEmail());
        }
        
        if (userUpdate.getUsername() != null && !userUpdate.getUsername().isEmpty()) {
            if (!user.getUsername().equals(userUpdate.getUsername()) && 
                userRepository.findByUsername(userUpdate.getUsername()).isPresent()) {
                throw new RuntimeException("Username này đã được sử dụng!");
            }
            user.setUsername(userUpdate.getUsername());
        }
        
        if (userUpdate.getFullName() != null) {
            user.setFullName(userUpdate.getFullName());
        }
        if (userUpdate.getAvatarUrl() != null) {
            user.setAvatarUrl(userUpdate.getAvatarUrl());
        }
        if (userUpdate.getProfileText() != null) {
            user.setProfileText(userUpdate.getProfileText());
        }
        if (userUpdate.getHeadline() != null) {
            user.setHeadline(userUpdate.getHeadline());
        }
        if (userUpdate.getSummary() != null) {
            user.setSummary(userUpdate.getSummary());
        }
        if (userUpdate.getLocation() != null) {
            user.setLocation(userUpdate.getLocation());
        }
        
        return userRepository.save(user);
    }

    public void delete(java.util.UUID id) {
        User user = this.getById(id);
        user.setDeleted(true);
        userRepository.save(user);
    }
}