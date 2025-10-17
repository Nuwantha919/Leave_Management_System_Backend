package com.example.leave_management_system.DTO;

import com.example.leave_management_system.Enums.LeaveStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class LeaveResponseDto {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;
    private Long employeeId;
    private String employeeName;
}