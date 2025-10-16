package com.example.leave_management_system.Services;

import com.example.leave_management_system.DTO.LoginRequest;
import com.example.leave_management_system.Entities.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

// This service handles the business logic for user authentication
@Service
public class AuthService {

    // --- In-Memory User Storage (for this stage) ---
    // In a real application, this logic would interact with a database (e.g., UserRepository)
    private final Map<String, User> inMemoryUsers = Map.of(
            "admin", new User("admin", "admin123", "ADMIN"),
            "employee", new User("employee", "emp123", "EMPLOYEE")
    );

    /**
     * Authenticates a user based on provided credentials.
     *
     * @param request The login request containing username and password.
     * @return An Optional containing the authenticated User, or empty if validation fails.
     */
    public Optional<User> authenticate(LoginRequest request) {
        // 1. Find the user by username
        Optional<User> userOptional = Optional.ofNullable(inMemoryUsers.get(request.getUsername()));

        // 2. Check if user exists and if the password matches
        // NOTE: In a real app, password comparison MUST use a strong encoder (e.g., BCryptPasswordEncoder)
        return userOptional
                .filter(user -> user.password().equals(request.getPassword()));
    }
}
