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
        log.debug("messageIs: " + mapper.convertValue(message, Map.class));
        recipients.forEach(user -> {
            try {
                if (ValidationString.isBlank(user.getFcmToken())) {
                    log.error("firebase cloud massaging token is absent; user: {} {}", user.getId(), user.getUsername());
                    return;
                }
                sendNotification(message.getEvent().name(), null, mapper.convertValue(message, Map.class), user.getFcmToken());
            } catch (FirebaseMessagingException e) {
                //todo add resendiong
                log.error("[notifyUsers] failed to notify user [{}]; reason : {}", user.getId(), e.getMessage());
            } catch (Exception e) {
                log.error("unexpected exception has occured : {}", e.getMessage());
            }
        });
    }


    public String sendNotification(String subject, String content, Map<String, String> data, String token) throws FirebaseMessagingException {

        Message message = Message
                .builder()
                .putAllData(data)
                .setToken(token)
                .build();

        log.trace("notification for {} is being sent", subject);
        log.debug("notificationIs: " + message);

        return firebaseMessaging.send(message);
    }
}

