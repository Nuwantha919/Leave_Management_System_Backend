package com.example.leave_management_system.Controllers;

import com.example.leave_management_system.DTO.LeaveRequestDto;
import com.example.leave_management_system.DTO.LeaveResponseDto;
import com.example.leave_management_system.Services.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    // POST /api/leaves — Create a leave request.
    @PostMapping
    public ResponseEntity<LeaveResponseDto> createLeave(@Valid @RequestBody LeaveRequestDto leaveRequestDto) {
        LeaveResponseDto createdLeave = leaveService.createLeave(leaveRequestDto);
        return new ResponseEntity<>(createdLeave, HttpStatus.CREATED);
    }

    // GET /api/leaves — Retrieve all leaves OR leaves for a specific employee.
    @GetMapping
    public ResponseEntity<List<LeaveResponseDto>> getAllLeaves(
            @RequestParam(required = false) String employeeName) {
        List<LeaveResponseDto> leaves;
        if (employeeName != null && !employeeName.isEmpty()) {
            leaves = leaveService.getLeavesByEmployeeName(employeeName);
        } else {
            leaves = leaveService.getAllLeaves();
        }
        return ResponseEntity.ok(leaves);
    }

    // GET /api/leaves/:id — Retrieve single leave.
    @GetMapping("/{id}")
    public ResponseEntity<LeaveResponseDto> getLeaveById(@PathVariable Long id) {
        LeaveResponseDto leave = leaveService.getLeaveById(id);
        return ResponseEntity.ok(leave);
    }

    // PUT /api/leaves/:id — Update leave.
    @PutMapping("/{id}")
    public ResponseEntity<LeaveResponseDto> updateLeave(@PathVariable Long id, @Valid @RequestBody LeaveRequestDto leaveRequestDto) {
        LeaveResponseDto updatedLeave = leaveService.updateLeave(id, leaveRequestDto);
        return ResponseEntity.ok(updatedLeave);
    }

    // DELETE /api/leaves/:id — Delete/cancel leave.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeave(@PathVariable Long id) {
        leaveService.deleteLeave(id);
        return ResponseEntity.noContent().build();
    }
}