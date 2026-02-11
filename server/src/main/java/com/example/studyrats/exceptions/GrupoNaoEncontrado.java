package com.example.studyrats.exceptions;

public class GrupoNaoEncontrado extends RuntimeException {
    public GrupoNaoEncontrado() {
        super("Grupo de estudo não encontrado");
    }
}
