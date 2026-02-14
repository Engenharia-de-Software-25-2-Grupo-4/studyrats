package com.example.studyrats.exceptions;
import com.example.studyrats.util.Mensagens;

public class GrupoNaoEncontrado extends RuntimeException {
    public GrupoNaoEncontrado() {
        super(Mensagens.GRUPO_NAO_ENCONTRADO);
    }
}
