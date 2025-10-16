package com.example.leave_management_system.Entities;

// A Java Record is perfect for immutable data classes like DTOs or simple entities
public record User(
        String username,
        String password, // Note: In a real application, this would be a hashed password (e.g., BCrypt)
        String role      // ADMIN or EMPLOYEE
) { }
