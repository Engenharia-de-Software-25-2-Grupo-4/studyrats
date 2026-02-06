package com.example.studyrats.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Retorna Erro 404
public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(String id) {
        super("Estudante com ID " + id + " não encontrado.");
    }
}