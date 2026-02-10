package com.example.studyrats.config;

import com.example.studyrats.exceptions.FirebaseJsonNaoEncontrado;
import org.springframework.context.annotation.Configuration;
import com.example.studyrats.exceptions.FirebaseIO;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import com.google.firebase.FirebaseApp;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccount = getClass()
                    .getClassLoader()
                    .getResourceAsStream("firebase/gymratsauth-firebase.json");

            if (serviceAccount == null) {
                throw new FirebaseJsonNaoEncontrado();
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

        } catch (IOException e) {
            throw new FirebaseIO();
        }
    }
}