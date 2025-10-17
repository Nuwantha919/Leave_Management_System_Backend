package com.example.leave_management_system.DTO;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequestDto {

    // REMOVED: private Long userId;
    // We get the user ID from the token on the backend now.

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