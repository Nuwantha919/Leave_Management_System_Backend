package com.example.leave_management_system.Repositories;

import com.example.leave_management_system.Entities.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    // Spring Data JPA automatically creates the query based on the method name.
    // This finds all leaves associated with a User entity whose username field matches.
    List<Leave> findByEmployee_Username(String username);

}