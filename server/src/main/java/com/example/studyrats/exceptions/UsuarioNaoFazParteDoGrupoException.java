package com.example.studyrats.exceptions;

import com.example.studyrats.util.Mensagens;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UsuarioNaoFazParteDoGrupoException extends RuntimeException {
    public UsuarioNaoFazParteDoGrupoException() {
        super(Mensagens.ESTUDANTE_NAO_FAZ_PARTE_DO_GRUPO);
    }
}
