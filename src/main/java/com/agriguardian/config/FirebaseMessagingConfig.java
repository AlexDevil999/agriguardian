package com.agriguardian.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseMessagingConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        try {
//            GoogleCredentials googleCredentials = GoogleCredentials
//                    .fromStream(new ClassPathResource("firebase-service-account.json").getInputStream());

            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new FileInputStream("firebase-service-account.json"));

            FirebaseOptions firebaseOptions = FirebaseOptions
                    .builder()
                    .setCredentials(googleCredentials)
                    .build();

            FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "AG-DEV");
            log.info("[firebaseMessaging] initializing of 'FirebaseMessaging' bean");
            return FirebaseMessaging.getInstance(app);
        } catch (IOException e) {
            log.error("[firebaseMessaging] init error [{}]", e.getMessage());
            log.warn("[firebaseMessaging] an important bean was not loaded. The application will be closed");
            System.exit(0);
        }
        return null;
    }
}
