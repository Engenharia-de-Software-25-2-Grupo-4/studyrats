package com.example.studyrats.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.studyrats.util.Mensagens;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UsuarioNaoAdmin extends RuntimeException{
    public UsuarioNaoAdmin() {
        super(Mensagens.ESTUDANTE_NAO_ADMIN);
    }
}
