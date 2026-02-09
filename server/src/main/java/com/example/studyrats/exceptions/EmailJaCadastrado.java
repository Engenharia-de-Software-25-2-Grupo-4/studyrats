package com.example.studyrats.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.studyrats.util.Mensagens;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailJaCadastrado extends RuntimeException {
    public EmailJaCadastrado() {
        super(Mensagens.EMAIL_JA_CADASTRADO);
    }
}