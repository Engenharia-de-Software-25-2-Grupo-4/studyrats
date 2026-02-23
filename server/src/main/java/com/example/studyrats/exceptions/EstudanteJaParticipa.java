package com.example.studyrats.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.studyrats.util.Mensagens;

@ResponseStatus(HttpStatus.CONFLICT)
public class EstudanteJaParticipa extends RuntimeException {
    public EstudanteJaParticipa() {
        super(Mensagens.ESTUDANTE_JA_PARTICIPA);
    }
}

