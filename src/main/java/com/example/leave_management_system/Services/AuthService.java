package com.example.leave_management_system.Services;

import com.example.leave_management_system.DTO.LoginRequest;
import com.example.leave_management_system.Entities.User;
import com.example.leave_management_system.Repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder; // <-- IMPORT THIS
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // <-- ADD THIS

    // Update the constructor to inject the PasswordEncoder
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder; // <-- ADD THIS
    }

    /**
     * Authenticates a user by securely comparing hashed passwords.
     *
     * @param request The login request containing username and plaintext password.
     * @return An Optional containing the User if authentication is successful.
     */
    public Optional<User> authenticate(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        // --- THIS IS THE REQUIRED CHANGE ---
        // Instead of user.getPassword().equals(request.getPassword()),
        // we use the encoder's "matches" method. This is the secure way.
        return userOptional
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()));
    }
}