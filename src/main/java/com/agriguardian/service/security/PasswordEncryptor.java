package com.agriguardian.service.security;

public interface PasswordEncryptor {
    String encode(String strToEncrypt);

    boolean matches(CharSequence rawString, String encodedString);

}
