package com.example.studyrats.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ComentarioNaoEncontradoException extends RuntimeException {
    public ComentarioNaoEncontradoException() {
        super("Comentário não encontrado.");
    }
}

