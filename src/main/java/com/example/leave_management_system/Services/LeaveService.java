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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final UserRepository userRepository;

    // POST /leaves
    public LeaveResponseDto createLeave(LeaveRequestDto leaveRequestDto) {
        if (leaveRequestDto.getEndDate().isBefore(leaveRequestDto.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        User employee = userRepository.findById(leaveRequestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + leaveRequestDto.getUserId()));

        Leave leave = Leave.builder()
                .employee(employee)
                .startDate(leaveRequestDto.getStartDate())
                .endDate(leaveRequestDto.getEndDate())
                .reason(leaveRequestDto.getReason())
                .status(LeaveStatus.PENDING) // New leaves are always pending
                .build();

        Leave savedLeave = leaveRepository.save(leave);
        return mapToDto(savedLeave);
    }

    // GET /leaves/:id
    public LeaveResponseDto getLeaveById(Long id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with ID: " + id));
        return mapToDto(leave);
    }

    // GET /leaves
    public List<LeaveResponseDto> getAllLeaves() {
        return leaveRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // GET /leaves?employee=employeeName
    public List<LeaveResponseDto> getLeavesByEmployeeName(String employeeName) {
        return leaveRepository.findByEmployee_Username(employeeName).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // PUT /leaves/:id
    public LeaveResponseDto updateLeave(Long id, LeaveRequestDto leaveRequestDto) {
        Leave existingLeave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with ID: " + id));

        // Update basic details
        existingLeave.setStartDate(leaveRequestDto.getStartDate());
        existingLeave.setEndDate(leaveRequestDto.getEndDate());
        existingLeave.setReason(leaveRequestDto.getReason());

        // Update status if provided (typically by an admin)
        if (leaveRequestDto.getStatus() != null && !leaveRequestDto.getStatus().isEmpty()) {
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

    // DELETE /leaves/:id
    public void deleteLeave(Long id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with ID: " + id));

        // Business rule: only allow deleting if it's pending
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Cannot delete a leave that is not in PENDING status.");
        }

        leaveRepository.delete(leave);
    }

    // Helper method to map Entity to DTO
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