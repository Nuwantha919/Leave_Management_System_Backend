package com.example.leave_management_system.Services;

import com.example.leave_management_system.DTO.LeaveRequestDto;
import com.example.leave_management_system.DTO.LeaveResponseDto;
import com.example.leave_management_system.Entities.Leave;
import com.example.leave_management_system.Entities.User;
import com.example.leave_management_system.Enums.LeaveStatus;
import com.example.leave_management_system.Repositories.LeaveRepository;
import com.example.leave_management_system.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// This extension initializes Mockito annotations
@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    // Mockito creates a mock object for the repository dependencies
    @Mock
    private LeaveRepository leaveRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Authentication authentication;

    // Mockito injects the mocks into this service instance
    @InjectMocks
    private LeaveService leaveService;

    // Test Data
    private User employee;
    private LeaveRequestDto validLeaveRequestDto;

    @BeforeEach
    void setUp() {
        // Setup shared test objects before each test method runs
        employee = new User();
        employee.setId(1L);
        employee.setUsername("testuser");
        employee.setRole("EMPLOYEE");

        validLeaveRequestDto = new LeaveRequestDto();
        validLeaveRequestDto.setStartDate(LocalDate.of(2024, 11, 1));
        validLeaveRequestDto.setEndDate(LocalDate.of(2024, 11, 5));
        validLeaveRequestDto.setReason("Vacation");

        // Common mock setup: The service always pulls the username from the token
        when(authentication.getName()).thenReturn(employee.getUsername());
    }

    // -------------------------------------------------------------------------
    // TEST CASES FOR createLeave (Focus on Business Logic & Conflict Detection)
    // -------------------------------------------------------------------------

    @Test
    void createLeave_shouldSucceed_whenRequestIsValidAndNoConflict() {
        // Arrange
        Leave savedLeave = Leave.builder()
                .id(10L).employee(employee).status(LeaveStatus.PENDING)
                .startDate(validLeaveRequestDto.getStartDate())
                .endDate(validLeaveRequestDto.getEndDate()).reason(validLeaveRequestDto.getReason()).build();

        when(userRepository.findByUsername(employee.getUsername())).thenReturn(Optional.of(employee));
        // Mocking the repository to find NO overlapping leaves (Success condition)
        when(leaveRepository.findOverlappingApprovedLeaves(any(User.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        when(leaveRepository.save(any(Leave.class))).thenReturn(savedLeave);

        // Act
        LeaveResponseDto result = leaveService.createLeave(validLeaveRequestDto, authentication);

        // Assert
        assertNotNull(result);
        assertEquals(LeaveStatus.PENDING, result.getStatus());
        verify(leaveRepository, times(1)).save(any(Leave.class));
    }

    @Test
    void createLeave_shouldThrowIllegalStateException_whenConflictWithApprovedLeave() {
        // Arrange: Overlapping leave (Nov 3 - Nov 6) conflicts with new request (Nov 1 - Nov 5)
        Leave conflictingLeave = Leave.builder()
                .id(9L).startDate(LocalDate.of(2024, 11, 3))
                .endDate(LocalDate.of(2024, 11, 6)).status(LeaveStatus.APPROVED).build();

        when(userRepository.findByUsername(employee.getUsername())).thenReturn(Optional.of(employee));
        // Mocking the repository to return a list with a conflict (Failure condition)
        when(leaveRepository.findOverlappingApprovedLeaves(any(User.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(conflictingLeave));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> leaveService.createLeave(validLeaveRequestDto, authentication));

        assertTrue(exception.getMessage().contains("Leave request conflicts with an existing APPROVED leave"));
        verify(leaveRepository, never()).save(any(Leave.class));
    }

    @Test
    void createLeave_shouldThrowIllegalArgumentException_whenEndDateIsBeforeStartDate() {
        // Arrange
        validLeaveRequestDto.setStartDate(LocalDate.of(2024, 11, 10));
        validLeaveRequestDto.setEndDate(LocalDate.of(2024, 11, 5)); // End date < Start date
        when(userRepository.findByUsername(employee.getUsername())).thenReturn(Optional.of(employee));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> leaveService.createLeave(validLeaveRequestDto, authentication));

        verify(leaveRepository, never()).save(any(Leave.class));
        // Ensure conflict detection is skipped if basic date validation fails
        verify(leaveRepository, never()).findOverlappingApprovedLeaves(any(), any(), any());
    }

    @Test
    void deleteLeave_shouldThrowIllegalStateException_whenStatusIsNotPending() {
        // Arrange
        Leave approvedLeave = Leave.builder()
                .id(1L).employee(employee).status(LeaveStatus.APPROVED).build();

        when(leaveRepository.findById(1L)).thenReturn(Optional.of(approvedLeave));
        when(authentication.getName()).thenReturn(employee.getUsername());
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList()); // Not an Admin

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> leaveService.deleteLeave(1L, authentication));

        verify(leaveRepository, never()).delete(any(Leave.class));
    }
}