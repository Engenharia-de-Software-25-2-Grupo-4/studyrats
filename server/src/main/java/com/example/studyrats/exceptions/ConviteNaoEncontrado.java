package com.example.studyrats.exceptions;
import com.example.studyrats.util.Mensagens;

public class ConviteNaoEncontrado extends RuntimeException {
    public ConviteNaoEncontrado() {
        super(Mensagens.CONVITE_NAO_ENCONTRADO);
    }
}
