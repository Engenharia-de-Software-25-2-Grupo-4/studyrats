package com.example.studyrats.exceptions;

import com.example.studyrats.util.Mensagens;

public class ArquivoNaoEhImagem extends RuntimeException {
    public ArquivoNaoEhImagem() {
        super(Mensagens.ARQUIVO_NAO_E_IMAGEM);
    }
}
