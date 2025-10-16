package com.example.leave_management_system.Services;

import com.example.leave_management_system.DTO.LoginRequest;
import com.example.leave_management_system.Entities.User;
import com.example.leave_management_system.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

// This service handles the business logic for user authentication
@Service
public class AuthService {

    // Inject the new UserRepository to interact with the MySQL database
    private final UserRepository userRepository;

    // Dependency Injection via constructor
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a user based on provided credentials by checking the database.
     *
     * @param request The login request containing username and password.
     * @return An Optional containing the authenticated User, or empty if validation fails.
     */
    public Optional<User> authenticate(LoginRequest request) {

        // 1. Find the user in the database by username
        // We use the custom method we defined in the UserRepository
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        // 2. Check if user exists and if the password matches
        // IMPORTANT: In a real app, password comparison MUST use a strong encoder (e.g., BCryptPasswordEncoder)
        return userOptional
                .filter(user -> user.getPassword().equals(request.getPassword()));
    }
}
