package com.example.leave_management_system.Controllers;

import com.example.leave_management_system.DTO.LoginRequest;
import com.example.leave_management_system.DTO.LoginResponse;
import com.example.leave_management_system.Entities.User;
import com.example.leave_management_system.Services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

// Controller for all authentication-related endpoints
@RestController
@RequestMapping("/login")
public class AuthController {

    private final AuthService authService;

    // Dependency injection via constructor (best practice)
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Handles the user login request (POST /login).
     *
     * @param request The LoginRequest DTO containing credentials.
     * @return ResponseEntity with a token/flag and user details, or UNAUTHORIZED status.
     */
    @PostMapping
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        Optional<User> authenticatedUser = authService.authenticate(request);

        if (authenticatedUser.isPresent()) {
            User user = authenticatedUser.get();

            // In this initial phase, we return a simple flag/message as the "token"
            // This will be replaced by actual JWT generation later.
            LoginResponse response = LoginResponse.builder()
                    .token("SESSION_FLAG_" + user.username().toUpperCase()) // Placeholder flag
                    .username(user.username())
                    .role(user.role())
                    .message("Login successful!")
                    .build();

            return ResponseEntity.ok(response);
        } else {
            // Standard practice for failed authentication
            LoginResponse errorResponse = LoginResponse.builder()
                    .message("Invalid username or password.")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}
