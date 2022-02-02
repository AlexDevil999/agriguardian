package com.agriguardian.service.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class BCryptEncryptor implements PasswordEncryptor {
    @Override
    public String encode(String strToEncrypt) {
        return new BCryptPasswordEncoder().encode(strToEncrypt);
    }

    @Override
    public boolean matches(CharSequence rawString, String encodedString) {
        return new BCryptPasswordEncoder().matches(rawString, encodedString);
    }



}
