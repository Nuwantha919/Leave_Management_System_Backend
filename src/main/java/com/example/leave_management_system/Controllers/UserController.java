package com.example.leave_management_system.Controllers;

import com.example.leave_management_system.DTO.UserRegistrationDto;
import com.example.leave_management_system.Entities.User;
import com.example.leave_management_system.Services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        User newUser = authService.createUser(registrationDto);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}