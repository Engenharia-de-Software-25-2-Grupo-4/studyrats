package com.example.studyrats.exceptions;

import com.example.studyrats.util.Mensagens;

public class UIDJaCadastrado extends RuntimeException {
    public UIDJaCadastrado() {
        super(Mensagens.UID_JA_CADASTRADO);
    }
}
