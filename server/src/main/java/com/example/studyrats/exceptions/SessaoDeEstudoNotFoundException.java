package com.example.studyrats.exceptions;

public class SessaoDeEstudoNotFoundException extends RuntimeException {
    public SessaoDeEstudoNotFoundException() {
        super("Sessao de estudo nao encontrada");
    }
}
