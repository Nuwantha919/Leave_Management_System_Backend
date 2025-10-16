package com.example.leave_management_system.DTO;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequestDto {

    // In a real app, you wouldn't pass this. You'd get it from the logged-in user's token.
    @NotNull(message = "Employee ID cannot be null")
    private Long userId;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must not be in the past")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must not be in the past")
    private LocalDate endDate;

    @NotEmpty(message = "Reason is required")
    private String reason;

    // This is optional, mainly for admin updates
    private String status;
}