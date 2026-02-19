package com.example.studyrats.exceptions;

import com.example.studyrats.util.Mensagens;

public class ExcecaoExemplo extends RuntimeException {
    public ExcecaoExemplo() {
        super(Mensagens.MENSAGEM_EXCEPTION_EXEMPLO);
    }
}
