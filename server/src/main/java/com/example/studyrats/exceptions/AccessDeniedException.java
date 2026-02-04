package com.example.studyrats.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("You don't have permission to perform this action.");
    }
    
}
