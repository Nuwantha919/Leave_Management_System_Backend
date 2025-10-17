package com.example.leave_management_system.Services;

import com.example.leave_management_system.DTO.LeaveRequestDto;
import com.example.leave_management_system.DTO.LeaveResponseDto;
import com.example.leave_management_system.Entities.Leave;
import com.example.leave_management_system.Entities.User;
import com.example.leave_management_system.Enums.LeaveStatus;
import com.example.leave_management_system.Repositories.LeaveRepository;
import com.example.leave_management_system.Repositories.UserRepository;
import com.example.leave_management_system.Utility.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final UserRepository userRepository;

    // POST /leaves (with security)
    public LeaveResponseDto createLeave(LeaveRequestDto leaveRequestDto, Authentication authentication) {
        String username = authentication.getName();
        User employee = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found in database: " + username));

        if (leaveRequestDto.getEndDate().isBefore(leaveRequestDto.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        // --- CONFLICT DETECTION: Check for overlapping APPROVED leaves ---
        List<Leave> overlappingLeaves = leaveRepository.findOverlappingApprovedLeaves(
                employee,
                leaveRequestDto.getStartDate(),
                leaveRequestDto.getEndDate()
        );

        if (!overlappingLeaves.isEmpty()) {
            // Throw an exception if a conflict is found.
            // The message provides details on the first found conflict.
            Leave conflict = overlappingLeaves.get(0);
            throw new IllegalStateException("Leave request conflicts with an existing APPROVED leave from " +
                    conflict.getStartDate() +
                    " to " + conflict.getEndDate());
        }
        // --- END CONFLICT DETECTION ---

        Leave leave = Leave.builder()
                .employee(employee)
                .startDate(leaveRequestDto.getStartDate())
                .endDate(leaveRequestDto.getEndDate())
                .reason(leaveRequestDto.getReason())
                .status(LeaveStatus.PENDING)
                .build();

        Leave savedLeave = leaveRepository.save(leave);
        return mapToDto(savedLeave);
    }

    // GET /leaves/:id (with security)
    public LeaveResponseDto getLeaveById(Long id, Authentication authentication) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with ID: " + id));
        authorizeAccess(authentication, leave);
        return mapToDto(leave);
    }

    // GET /leaves (no security needed here, handled in controller)
    public List<LeaveResponseDto> getAllLeaves() {
        return leaveRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // GET /leaves?employee=... (no security needed here)
    public List<LeaveResponseDto> getLeavesByEmployeeName(String employeeName) {
        return leaveRepository.findByEmployee_Username(employeeName).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // PUT /leaves/:id (with security)
    public LeaveResponseDto updateLeave(Long id, LeaveRequestDto leaveRequestDto, Authentication authentication) {
        Leave existingLeave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with ID: " + id));
        authorizeAccess(authentication, existingLeave);

        if (!isAdmin(authentication) && existingLeave.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("You can only update leaves that are in PENDING status.");
        }

        existingLeave.setStartDate(leaveRequestDto.getStartDate());
        existingLeave.setEndDate(leaveRequestDto.getEndDate());
        existingLeave.setReason(leaveRequestDto.getReason());

        if (isAdmin(authentication) && leaveRequestDto.getStatus() != null && !leaveRequestDto.getStatus().isEmpty()) {
            try {
                LeaveStatus newStatus = LeaveStatus.valueOf(leaveRequestDto.getStatus().toUpperCase());
                existingLeave.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid leave status: " + leaveRequestDto.getStatus());
            }
        }

        Leave updatedLeave = leaveRepository.save(existingLeave);
        return mapToDto(updatedLeave);
    }

    // DELETE /leaves/:id (with security)
    public void deleteLeave(Long id, Authentication authentication) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with ID: " + id));
        authorizeAccess(authentication, leave);

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Cannot delete a leave that is not in PENDING status.");
        }

        leaveRepository.delete(leave);
    }

    /**
     * Updates only the status of a leave request (Admin/Manager action).
     * @param id The ID of the leave.
     * @param newStatusString The new status as a string ("APPROVED" or "REJECTED").
     * @return The updated leave DTO.
     */
    public LeaveResponseDto updateLeaveStatus(Long id, String newStatusString) {
        Leave existingLeave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with ID: " + id));

        try {
            LeaveStatus newStatus = LeaveStatus.valueOf(newStatusString.toUpperCase());
            existingLeave.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid leave status: " + newStatusString);
        }

        // NOTE: A more complex system would check for conflicts here if status is set to APPROVED,
        // but for basic implementation, we skip it.

        Leave updatedLeave = leaveRepository.save(existingLeave);
        return mapToDto(updatedLeave);
    }

    // --- Helper Methods ---
    private void authorizeAccess(Authentication authentication, Leave leave) {
        String loggedInUsername = authentication.getName();
        String leaveOwnerUsername = leave.getEmployee().getUsername();

        if (!isAdmin(authentication) && !loggedInUsername.equals(leaveOwnerUsername)) {
            throw new AccessDeniedException("You do not have permission to access this resource.");
        }
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ADMIN"));
    }

    private LeaveResponseDto mapToDto(Leave leave) {
        return LeaveResponseDto.builder()
                .id(leave.getId())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .reason(leave.getReason())
                .status(leave.getStatus())
                .employeeId(leave.getEmployee().getId())
                .employeeName(leave.getEmployee().getUsername())
                .build();
    }
}