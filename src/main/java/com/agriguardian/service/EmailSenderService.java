package com.agriguardian.service;

import com.agriguardian.exception.InternalErrorException;
import com.agriguardian.service.interfaces.EmailSender;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
@Slf4j
public class EmailSenderService implements EmailSender {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("authforagriguardian@gmail.com");

            log.debug("[send] mailSender is sending message for {}", to);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("failed to send an email. Reason: " + e.getMessage());
            throw new InternalErrorException("unexpected server error");
        }
    }

}
