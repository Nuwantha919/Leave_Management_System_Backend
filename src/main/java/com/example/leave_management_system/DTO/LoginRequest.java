package com.example.leave_management_system.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Data Transfer Object for incoming login request (username and password)
@Data // Lombok annotation for getters, setters, toString, equals, and hashCode
public class LoginRequest {

    // Simple validation to ensure fields are not empty or null
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
