package com.example.studyrats.exceptions;
import com.example.studyrats.util.Mensagens;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GrupoNaoEncontrado extends RuntimeException {
    public GrupoNaoEncontrado() {
        super(Mensagens.GRUPO_NAO_ENCONTRADO);
    }
}
