package com.example.leave_management_system.Controllers;

import com.example.leave_management_system.DTO.LoginRequest;
import com.example.leave_management_system.DTO.LoginResponse;
import com.example.leave_management_system.Entities.User;
import com.example.leave_management_system.Services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// Controller for all authentication-related endpoints
@RestController
@RequestMapping("/login")
@CrossOrigin("*")
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

            String tokenValue = String.format("SESSION_FLAG_%s_%s",
                    user.getRole().toUpperCase(),
                    user.getUsername());

            LoginResponse response = LoginResponse.builder()
                    .token(tokenValue)
                    .username(user.getUsername()) // Using getUsername()
                    .role(user.getRole()) // Using getRole()
                    .message("Login successful!")
                    .userId(user.getId())
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
