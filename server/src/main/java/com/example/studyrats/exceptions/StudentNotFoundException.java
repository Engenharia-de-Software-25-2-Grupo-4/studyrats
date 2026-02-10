package com.example.studyrats.exceptions;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(String studentId) {
        super("Student nao encontrado: " + studentId);
    }
}
