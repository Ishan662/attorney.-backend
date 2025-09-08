package com.example.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseChatConfig {

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        // This looks for the "serviceAccountKey.json" you placed in src/main/resources
        ClassPathResource serviceAccountResource = new ClassPathResource("serviceAccountKey.json");

        try (InputStream serviceAccount = serviceAccountResource.getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // This logic prevents the app from crashing on a hot reload or in tests
            // by checking if Firebase has already been initialized.
            if (FirebaseApp.getApps().isEmpty()) {
                return FirebaseApp.initializeApp(options);
            } else {
                return FirebaseApp.getInstance();
            }
        }
    }
}