package com.example.leave_management_system.Controllers;

import com.example.leave_management_system.DTO.LeaveRequestDto;
import com.example.leave_management_system.DTO.LeaveResponseDto;
import com.example.leave_management_system.DTO.LeaveStatusUpdateDto;
import com.example.leave_management_system.Services.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    // POST /api/leaves — Create a leave request.
    @PostMapping
    public ResponseEntity<LeaveResponseDto> createLeave(
            @Valid @RequestBody LeaveRequestDto leaveRequestDto,
            Authentication authentication // Inject authentication object
    ) {
        // Pass the DTO and the logged-in user's details to the service
        LeaveResponseDto createdLeave = leaveService.createLeave(leaveRequestDto, authentication);
        return new ResponseEntity<>(createdLeave, HttpStatus.CREATED);
    }

    // GET /api/leaves
    @GetMapping
    public ResponseEntity<List<LeaveResponseDto>> getAllLeaves(
            @RequestParam(required = false) String employeeName,
            Authentication authentication
    ) {
        String loggedInUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ADMIN"));

        List<LeaveResponseDto> leaves;

        if (isAdmin) {
            if (employeeName != null && !employeeName.isEmpty()) {
                leaves = leaveService.getLeavesByEmployeeName(employeeName);
            } else {
                leaves = leaveService.getAllLeaves();
            }
        } else {
            leaves = leaveService.getLeavesByEmployeeName(loggedInUsername);
        }
        return ResponseEntity.ok(leaves);
    }

    // GET /api/leaves/:id
    @GetMapping("/{id}")
    public ResponseEntity<LeaveResponseDto> getLeaveById(@PathVariable Long id, Authentication authentication) {
        LeaveResponseDto leave = leaveService.getLeaveById(id, authentication);
        return ResponseEntity.ok(leave);
    }

    // PUT /api/leaves/:id
    @PutMapping("/{id}")
    public ResponseEntity<LeaveResponseDto> updateLeave(
            @PathVariable Long id,
            @Valid @RequestBody LeaveRequestDto leaveRequestDto,
            Authentication authentication
    ) {
        LeaveResponseDto updatedLeave = leaveService.updateLeave(id, leaveRequestDto, authentication);
        return ResponseEntity.ok(updatedLeave);
    }

    // DELETE /api/leaves/:id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeave(@PathVariable Long id, Authentication authentication) {
        leaveService.deleteLeave(id, authentication);
        return ResponseEntity.noContent().build();
    }
    /**
     * SPECIFIC endpoint for an Admin to update a leave's status.
     * @param id The ID of the leave.
     * @param statusUpdateDto The DTO containing the new status.
     * @return The updated leave.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<LeaveResponseDto> updateLeaveStatus(
            @PathVariable Long id,
            @Valid @RequestBody LeaveStatusUpdateDto statusUpdateDto) {
        LeaveResponseDto updatedLeave = leaveService.updateLeaveStatus(id, statusUpdateDto.getStatus());
        return ResponseEntity.ok(updatedLeave);
    }
}