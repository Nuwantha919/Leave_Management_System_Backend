package com.example.leave_management_system.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents a User entity, mapped to a table in the MySQL database.
 */
@Entity
@Table(name = "users")
@Data // Lombok: Generates getters, setters, equals, hashCode, and toString
@NoArgsConstructor // Lombok: Generates a constructor with no arguments (required by JPA)
@AllArgsConstructor // Lombok: Generates a constructor with all arguments
public class User {

    // Primary Key (ID) for the database table
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increments in MySQL
    private Long id;

    // Field mapping to the 'username' column
    @Column(nullable = false, unique = true)
    private String username;

    // Field mapping to the 'password' column
    @Column(nullable = false)
    private String password;

    // Field mapping to the 'role' column (e.g., ADMIN, EMPLOYEE)
    @Column(nullable = false)
    private String role;

    // We keep a custom constructor for initial creation (without the auto-generated ID)
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
