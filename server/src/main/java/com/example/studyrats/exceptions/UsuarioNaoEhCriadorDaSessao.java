package com.example.studyrats.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.studyrats.util.Mensagens;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UsuarioNaoEhCriadorDaSessao extends RuntimeException {
    public UsuarioNaoEhCriadorDaSessao() {
        super(Mensagens.USUARIO_NAO_EH_CRIADOR_DA_SESSAO);
    }
    
}
