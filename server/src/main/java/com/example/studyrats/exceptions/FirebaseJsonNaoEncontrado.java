package com.example.studyrats.exceptions;

import com.example.studyrats.util.Mensagens;

public class FirebaseJsonNaoEncontrado extends RuntimeException {
    public FirebaseJsonNaoEncontrado() {
        super(Mensagens.FIREBASE_JSON_NAO_ENCONTRADO);
    }
}
