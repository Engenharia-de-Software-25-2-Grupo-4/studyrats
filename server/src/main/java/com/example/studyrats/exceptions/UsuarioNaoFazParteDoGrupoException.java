package com.example.studyrats.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UsuarioNaoFazParteDoGrupoException extends RuntimeException {
    public UsuarioNaoFazParteDoGrupoException() {
        super("O usuário não faz parte do grupo de estudo.");
    }
}
