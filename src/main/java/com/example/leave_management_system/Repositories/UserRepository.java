package com.example.leave_management_system.Repositories;

import com.example.leave_management_system.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * JpaRepository provides CRUD (Create, Read, Update, Delete) operations.
 */
@Repository
// <Entity Class, Data Type of the Primary Key>
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Custom method to find a User by their username.
     * Spring Data JPA automatically generates the SQL query from the method name!
     * @param username The username to search for.
     * @return An Optional containing the User if found.
     */
    Optional<User> findByUsername(String username);
}
