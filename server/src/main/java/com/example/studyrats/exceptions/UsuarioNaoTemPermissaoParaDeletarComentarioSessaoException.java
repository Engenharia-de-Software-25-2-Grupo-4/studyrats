package com.example.studyrats.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UsuarioNaoTemPermissaoParaDeletarComentarioSessaoException extends RuntimeException {
    public UsuarioNaoTemPermissaoParaDeletarComentarioSessaoException() {
        super("O usuário não tem permissão para deletar este comentário.");
    }   
}
