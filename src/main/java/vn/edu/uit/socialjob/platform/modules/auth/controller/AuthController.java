package vn.edu.uit.socialjob.platform.modules.auth.controller;

import com.google.firebase.auth.FirebaseAuthException;

import lombok.extern.slf4j.Slf4j;
import vn.edu.uit.socialjob.platform.modules.auth.dto.AuthResponse;
import vn.edu.uit.socialjob.platform.modules.auth.dto.DevLoginRequest;
import vn.edu.uit.socialjob.platform.modules.auth.dto.FirebaseAuthRequest;
import vn.edu.uit.socialjob.platform.modules.auth.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/firebase")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody FirebaseAuthRequest request) throws FirebaseAuthException {
        AuthResponse response = authService.authenticateWithFirebase(request.getIdToken());
        log.info("Đăng nhập thành công cho User: {}", response.getUser().getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * DEV ONLY: Login without Firebase for testing
     * Usage: POST /api/auth/dev-login { "email": "test@example.com" }
     * Returns: JWT token that can be used with Authorization: Bearer <token>
     */
    @PostMapping("/dev-login")
    public ResponseEntity<AuthResponse> devLogin(@RequestBody DevLoginRequest request) {
        log.warn("DEV MODE: Generating token for email: {}", request.getEmail());
        AuthResponse response = authService.authenticateForDev(request.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
