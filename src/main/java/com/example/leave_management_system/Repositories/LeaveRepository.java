package com.example.leave_management_system.Repositories;

import com.example.leave_management_system.Entities.Leave;
import com.example.leave_management_system.Entities.User;
import com.example.leave_management_system.Enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    List<Leave> findByEmployee_Username(String username);

    List<Leave> findByEmployeeAndStatus(User employee, LeaveStatus status);
}