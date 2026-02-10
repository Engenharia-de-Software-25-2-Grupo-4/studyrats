package com.example.studyrats.exceptions;

public class SessaoDeEstudoNaoEncontrado extends RuntimeException {
    public SessaoDeEstudoNaoEncontrado() {
        super("Sessao de estudo nao encontrada");
    }
}
