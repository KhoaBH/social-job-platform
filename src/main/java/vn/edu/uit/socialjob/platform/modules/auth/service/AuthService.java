package vn.edu.uit.socialjob.platform.modules.auth.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;

import vn.edu.uit.socialjob.platform.modules.auth.dto.AuthResponse;
import vn.edu.uit.socialjob.platform.modules.auth.entity.UserAuth;
import vn.edu.uit.socialjob.platform.modules.auth.repository.UserAuthRepository;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.repository.UserRepository;
import vn.edu.uit.socialjob.platform.modules.auth.provider.JwtProvider;
@Service
public class AuthService {
    private static final String PROVIDER_FIREBASE = "firebase";

    private final FirebaseAuth firebaseAuth;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;

    public AuthService(
        FirebaseAuth firebaseAuth,
        JwtProvider jwtProvider,
        UserRepository userRepository,
        UserAuthRepository userAuthRepository
    ) {
        this.firebaseAuth = firebaseAuth;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.userAuthRepository = userAuthRepository;
    }

    public AuthResponse authenticateWithFirebase(String idToken) throws FirebaseAuthException {
        FirebaseToken token = firebaseAuth.verifyIdToken(idToken);

        String firebaseUid = token.getUid();
        String email = token.getEmail();
        String fullName = token.getName();
        String avatarUrl = token.getPicture();

        User user = findOrCreateUser(email, fullName, avatarUrl, firebaseUid);
        ensureUserAuth(user, firebaseUid);
        String jwtToken = jwtProvider.generateToken(user);
        return AuthResponse.builder()
            .token(jwtToken)
            .user(AuthResponse.UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .build())
            .build();
    }

    /**
     * DEV ONLY: Generate JWT token without Firebase authentication
     * Used for testing backend APIs without frontend/Firebase login
     */
    public AuthResponse authenticateForDev(String email) {
        Optional<User> existing = userRepository.findByEmail(email);
        User user;
        
        if (existing.isPresent()) {
            user = existing.get();
        } else {
            // Create new test user
            user = new User();
            user.setEmail(email);
            user.setUsername(generateUsername(email, null));
            user.setFullName("Test User - " + email);
            user.setAvatarUrl("https://ui-avatars.com/api/?name=" + email);
            user = userRepository.save(user);
        }
        
        String jwtToken = jwtProvider.generateToken(user);
        return AuthResponse.builder()
            .token(jwtToken)
            .user(AuthResponse.UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .build())
            .build();
    }

    private User findOrCreateUser(String email, String fullName, String avatarUrl, String firebaseUid) {
        Optional<User> existing = (email == null || email.isBlank())
            ? Optional.empty()
            : userRepository.findByEmail(email);

        if (existing.isPresent()) {
            return existing.get();
        }

        User user = new User();
        user.setEmail(email != null ? email : ("firebase_" + firebaseUid + "@local"));
        user.setUsername(generateUsername(email, firebaseUid));
        user.setFullName(fullName);
        user.setAvatarUrl(avatarUrl);
        return userRepository.save(user);
    }

    private void ensureUserAuth(User user, String firebaseUid) {
        Optional<UserAuth> existing = userAuthRepository
            .findByProviderAndFirebaseUid(PROVIDER_FIREBASE, firebaseUid);

        if (existing.isPresent()) {
            return;
        }

        UserAuth auth = new UserAuth();
        auth.setUser(user);
        auth.setProvider(PROVIDER_FIREBASE);
        auth.setFirebaseUid(firebaseUid);
        auth.setLinkedAt(LocalDateTime.now());
        userAuthRepository.save(auth);
    }

    private String generateUsername(String email, String firebaseUid) {
        String base = (email != null && !email.isBlank())
            ? email.split("@", 2)[0]
            : "firebase_" + firebaseUid;

        base = base.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]+", "");
        if (base.isBlank()) {
            base = "user";
        }

        String candidate = base;
        int counter = 1;
        while (userRepository.findByUsername(candidate).isPresent()) {
            candidate = base + counter;
            counter += 1;
        }

        return candidate;
    }
}
