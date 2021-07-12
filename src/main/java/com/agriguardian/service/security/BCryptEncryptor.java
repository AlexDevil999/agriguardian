package com.agriguardian.service.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class BCryptEncryptor implements PasswordEncryptor {
    private final BCryptPasswordEncoder bcrypt;

    @Override
    public String encode(String strToEncrypt) {
        return bcrypt.encode(strToEncrypt);
    }

    @Override
    public boolean matches(CharSequence rawString, String encodedString) {
        return bcrypt.matches(rawString, encodedString);
    }



    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
