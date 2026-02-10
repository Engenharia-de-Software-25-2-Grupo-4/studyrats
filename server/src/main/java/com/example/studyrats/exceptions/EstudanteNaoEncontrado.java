package com.example.studyrats.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.studyrats.util.Mensagens;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EstudanteNaoEncontrado extends RuntimeException {
    public EstudanteNaoEncontrado() {
        super(Mensagens.ESTUDANTE_NAO_ENCONTRADO);
    }
}