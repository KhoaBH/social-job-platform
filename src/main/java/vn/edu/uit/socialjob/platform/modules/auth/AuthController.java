package vn.edu.uit.socialjob.platform.modules.auth;

import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.socialjob.platform.modules.user.User;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/firebase")
    public ResponseEntity<User> authenticate(@RequestBody FirebaseAuthRequest request) throws FirebaseAuthException {
        User user = authService.authenticateWithFirebase(request.getIdToken());
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
