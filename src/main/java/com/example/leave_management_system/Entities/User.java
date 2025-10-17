package com.example.leave_management_system.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashSet; // <-- IMPORT THIS
import java.util.Set;      // <-- AND THIS

/**
 * Represents a User entity, mapped to a table in the MySQL database.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    // --- THIS IS THE REQUIRED FIX ---
    // This tells Hibernate that one User can have many Leaves.
    // Initializing it with new HashSet<>() prevents the startup crash.
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Leave> leaves = new HashSet<>();

    @Column(nullable = false, columnDefinition = "INT DEFAULT 20")
    private int maximumLeaveCount = 20;

    // Custom constructor
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}