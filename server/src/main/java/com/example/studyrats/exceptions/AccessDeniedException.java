package com.example.studyrats.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("Voce nao tem permissao para acessar este recurso");
    }
}
