package com.example.studyrats.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.studyrats.util.Mensagens;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class EstudanteNaoAutenticado extends RuntimeException {
    public EstudanteNaoAutenticado() {
        super(Mensagens.ESTUDANTE_NAO_AUTENTICADO);
    }
}