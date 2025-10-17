package com.example.leave_management_system.Repositories;

import com.example.leave_management_system.Entities.Leave;
import com.example.leave_management_system.Entities.User;
import com.example.leave_management_system.Enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    List<Leave> findByEmployee_Username(String username);

    List<Leave> findByEmployeeAndStatus(User employee, LeaveStatus status);

    /**
     * Finds any APPROVED leave requests for a given employee that overlap with the specified date range.
     * Overlap condition: (start1 <= end2) AND (end1 >= start2)
     * Here: (existingLeave.startDate <= newLeave.endDate) AND (existingLeave.endDate >= newLeave.startDate)
     */
    @Query("SELECT l FROM Leave l " +
            "WHERE l.employee = :employee " +
            "AND l.status = 'APPROVED' " +
            "AND l.startDate <= :endDate " +
            "AND l.endDate >= :startDate")
    List<Leave> findOverlappingApprovedLeaves(
            @Param("employee") User employee,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}