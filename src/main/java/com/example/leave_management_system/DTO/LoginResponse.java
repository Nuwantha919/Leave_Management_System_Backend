package com.example.leave_management_system.DTO;

import lombok.Builder;
import lombok.Data;

// Data Transfer Object for outgoing login response
@Data
@Builder // Lombok annotation to use the Builder pattern for easy object creation
public class LoginResponse {
    private String token; // Will hold the session flag or JWT
    private String username;
    private String role;
    private String message;
    private Long userId;
}
