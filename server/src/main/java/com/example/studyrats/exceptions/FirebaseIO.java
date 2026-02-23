package com.example.studyrats.exceptions;

import com.example.studyrats.util.Mensagens;

public class FirebaseIO extends RuntimeException {
    public FirebaseIO() {
        super(Mensagens.FIREBASE_IO);
    }
}
