package com.example.leave_management_system.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LeaveStatusUpdateDto {
    @NotBlank(message = "Status cannot be blank")
    private String status;
}