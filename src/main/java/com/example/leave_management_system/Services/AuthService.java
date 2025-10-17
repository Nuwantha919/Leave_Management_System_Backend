// src/main/java/com/example/leave_management_system/Services/AuthService.java

package com.example.leave_management_system.Services;

import com.example.leave_management_system.DTO.LoginRequest;
import com.example.leave_management_system.DTO.UserRegistrationDto;
import com.example.leave_management_system.Entities.Leave;
import com.example.leave_management_system.Entities.User;
import com.example.leave_management_system.Enums.LeaveStatus;
import com.example.leave_management_system.Repositories.LeaveRepository;
import com.example.leave_management_system.Repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LeaveRepository leaveRepository; // Add this

    // Update constructor
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, LeaveRepository leaveRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.leaveRepository = leaveRepository;
    }

    // Your original authenticate method (no changes)
    public Optional<User> authenticate(LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()));
    }

    // Update createUser method
    public User createUser(UserRegistrationDto registrationDto) {
        if (userRepository.findByUsername(registrationDto.getUsername()).isPresent()) {
            throw new IllegalStateException("Username '" + registrationDto.getUsername() + "' already exists.");
        }
        User newUser = new User();
        newUser.setUsername(registrationDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        newUser.setRole(registrationDto.getRole().toUpperCase());
        newUser.setMaximumLeaveCount(registrationDto.getMaximumLeaveCount()); // Set new field
        return userRepository.save(newUser);
    }

    // New method to calculate leaves
    public long calculateLeavesTaken(User user) {
        List<Leave> approvedLeaves = leaveRepository.findByEmployeeAndStatus(user, LeaveStatus.APPROVED);
        long totalDaysTaken = 0;
        for (Leave leave : approvedLeaves) {
            totalDaysTaken += ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
        }
        return totalDaysTaken;
    }
}