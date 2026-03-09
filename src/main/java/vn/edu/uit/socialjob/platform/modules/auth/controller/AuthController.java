package vn.edu.uit.socialjob.platform.modules.auth.controller;

import com.google.firebase.auth.FirebaseAuthException;

import vn.edu.uit.socialjob.platform.modules.auth.dto.FirebaseAuthRequest;
import vn.edu.uit.socialjob.platform.modules.auth.service.AuthService;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
