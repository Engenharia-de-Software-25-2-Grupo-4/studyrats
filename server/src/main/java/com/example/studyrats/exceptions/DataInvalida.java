package com.example.studyrats.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.studyrats.util.Mensagens;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DataInvalida extends RuntimeException{
    public DataInvalida() {
        super(Mensagens.EMAIL_JA_CADASTRADO);
    }
}
