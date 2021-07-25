package com.agriguardian.service;

import com.agriguardian.dto.MessageDto;
import com.agriguardian.entity.AppUser;
import com.agriguardian.service.interfaces.Notificator;
import com.agriguardian.util.ValidationString;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Firebase cloud messaging notification
 */
@Slf4j
@Service
@AllArgsConstructor
public class FcmNotificator implements Notificator {
    private final FirebaseMessaging firebaseMessaging;
    private final ObjectMapper mapper;


    @Override
    public void notifyUsers(Set<AppUser> recipients, MessageDto message) {
            recipients.forEach(user -> {
                try {
                    if (ValidationString.isBlank(user.getFcmToken())) {
                        log.error("firebase cloud massaging token is absent; user: {} {}", user.getId(), user.getUsername());
                        return;
                    }
                    sendNotification(message.getEvent().name(), mapper.writeValueAsString(message), null, user.getFcmToken());
                } catch (FirebaseMessagingException | JsonProcessingException e) {
                    //todo add resendiong
                    log.error("[notifyUsers] failed to notify user [{}]; reason : {}", user.getId(), e.getMessage());
                }
            });
    }


    public String sendNotification(String subject, String content, Map<String, String> data, String token) throws FirebaseMessagingException {

        Notification notification = Notification
                .builder()
                .setTitle(subject)
                .setBody(content)
                .build();


        Message message = Message
                .builder()
                .setToken(token)
                .setNotification(notification)
//                .putAllData(data)
                .build();

        return firebaseMessaging.send(message);
    }

//    public void sendPersonal(String clientToken, String origin) {
//        Message message = Message.builder()
//                .setNotification(Notification.builder()
//                        .setTitle("SNFY")
//                        .setBody(origin)
//                        .build())
//                .setToken(clientToken)
//                .setApnsConfig(ApnsConfig.builder()
//                        .putHeader("content-available", "1")
//                        .setAps(Aps.builder().build())
//                        .build())
//                .build();
//
//        try {
//            FirebaseMessaging.getInstance().sendAsync(message).get();
//        } catch (InterruptedException | ExecutionException e) {
//            log.warn("{} [{}]", e.getClass().getSimpleName(), e.getMessage());
//        }
//    }
}

//@Log4j2
//@Service
//public class Notificator {
//
////    public Notificator() {
////        try {
////            FirebaseOptions options = FirebaseOptions.builder()
////                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream("fire.key.json"))).build();
////            FirebaseApp.initializeApp(options);
////        } catch (IOException e) {
////            log.warn("Firebase init error [{}]", e.getMessage());
////        }
////    }
//
////    public void sendPersonal(String clientToken, String origin) {
////        Message message = Message.builder()
////                .setNotification(Notification.builder()
////                        .setTitle("SNFY")
////                        .setBody(origin)
////                        .build())
////                .setToken(clientToken)
////                .setApnsConfig(ApnsConfig.builder()
////                        .putHeader("content-available", "1")
////                        .setAps(Aps.builder().build())
////                        .build())
////                .build();
////
////        try {
////            FirebaseMessaging.getInstance().sendAsync(message).get();
////        } catch (InterruptedException | ExecutionException e) {
////            log.warn("{} [{}]", e.getClass().getSimpleName(), e.getMessage());
////        }
////    }
//}
