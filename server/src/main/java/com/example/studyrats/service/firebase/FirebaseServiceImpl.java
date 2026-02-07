package com.example.studyrats.service.firebase;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseServiceImpl implements FirebaseService {

    @Override
    public FirebaseToken verifyToken(String token) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(token);
    }
}
