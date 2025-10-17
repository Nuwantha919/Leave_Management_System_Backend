// src/main/java/com/example/leave_management_system/Controllers/AuthController.java

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

@RestController
@RequestMapping("/login")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Optional<User> authenticatedUser = authService.authenticate(request);

        if (authenticatedUser.isPresent()) {
            User user = authenticatedUser.get();
            String tokenValue = String.format("SESSION_FLAG_%s_%s", user.getRole().toUpperCase(), user.getUsername());

            // Calculate leave data
            long leavesTaken = authService.calculateLeavesTaken(user);
            long leaveBalance = user.getMaximumLeaveCount() - leavesTaken;

            LoginResponse response = LoginResponse.builder()
                    .token(tokenValue)
                    .username(user.getUsername())
                    .role(user.getRole())
                    .message("Login successful!")
                    .userId(user.getId())
                    .maximumLeaveCount(user.getMaximumLeaveCount())
                    .leavesTaken(leavesTaken)
                    .leaveBalance(leaveBalance)
                    .build();

            return ResponseEntity.ok(response);
        } else {
            LoginResponse errorResponse = LoginResponse.builder()
                    .message("Invalid username or password.")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}