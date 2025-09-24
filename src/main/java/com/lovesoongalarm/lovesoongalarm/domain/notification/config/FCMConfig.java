package com.lovesoongalarm.lovesoongalarm.domain.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FCMConfig {

    @Value("${firebase.config-file}")
    private String firebaseConfigPath;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        log.info("Firebase 설정 초기화 시작");

        try (InputStream serviceAccount = new ClassPathResource(firebaseConfigPath).getInputStream()) {
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccount);

            FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                    .setCredentials(googleCredentials)
                    .build();

            // FirebaseApp이 이미 초기화되어 있는지 확인
            FirebaseApp app;
            if (FirebaseApp.getApps().isEmpty()) {
                app = FirebaseApp.initializeApp(firebaseOptions, "love-soong-alarm");
                log.info("Firebase 앱 초기화 완료");
            } else {
                app = FirebaseApp.getInstance("love-soong-alarm");
                log.info("기존 Firebase 앱 사용");
            }

            return FirebaseMessaging.getInstance(app);
        } catch (Exception e) {
            log.error("Firebase 설정 실패", e);
            throw e;
        }
    }
}
