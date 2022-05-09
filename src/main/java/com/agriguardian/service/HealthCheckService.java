package com.agriguardian.service;

import com.agriguardian.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthCheckService {
//    @Value("${emails.first}")
    private String email;
//    @Value("${emails.second}")
    private String additionalEmail;
    private Long currentUsers;
    private final AppUserRepository userRepository;
    private final EmailSenderService emailSenderService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendReportAboutService(){
        if(currentUsers==null)
            currentUsers=userRepository.count();

        userRepository.count();
        emailSenderService.send(email,"service is working. " + (userRepository.count()-currentUsers) + "new users since last email","health check");
        emailSenderService.send(additionalEmail,"service is working. " + (userRepository.count()-currentUsers) + "new users since last email", "health check");
    }
}
