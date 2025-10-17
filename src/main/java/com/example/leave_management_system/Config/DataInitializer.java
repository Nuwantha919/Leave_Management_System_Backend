package com.example.leave_management_system.Config;

import com.example.leave_management_system.Entities.User;
import com.example.leave_management_system.Repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * This component runs on application startup to initialize default database records.
 * It ensures that essential users (like an admin) are always present.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create Admin User if one doesn't exist
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin123"), // Always encode passwords!
                    "ADMIN" // Use uppercase to match security rules
            );
            userRepository.save(admin);
            System.out.println("Default ADMIN user created.");
        }

        // Create Employee User if one doesn't exist
        if (userRepository.findByUsername("employee").isEmpty()) {
            User employee = new User(
                    "employee",
                    passwordEncoder.encode("emp123"), // Always encode passwords!
                    "EMPLOYEE" // Use uppercase to match security rules
            );
            userRepository.save(employee);
            System.out.println("Default EMPLOYEE user created.");
        }
    }
}