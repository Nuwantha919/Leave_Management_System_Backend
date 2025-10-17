package com.example.leave_management_system.Utility;

// REMOVE the @ResponseStatus annotation from here.
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}