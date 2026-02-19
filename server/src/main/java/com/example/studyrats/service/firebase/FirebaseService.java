package com.example.studyrats.service.firebase;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

public interface FirebaseService {
    public FirebaseToken verifyToken(String token) throws FirebaseAuthException;
}
