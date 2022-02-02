package com.agriguardian.service;

import com.agriguardian.exception.InternalErrorException;
import com.agriguardian.service.interfaces.DataEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Service
@Slf4j
public class AesEncryptor implements DataEncoder {
    private String keyStr;

    private static byte[] key;
    private static SecretKeySpec secretKey;

    public AesEncryptor(@Value("${aes.key}")String keyStr) {
        this.keyStr=keyStr;
        setKey();
    }

    private void setKey(){
        if(secretKey!=null)
            return;
        MessageDigest sha = null;
        try {
            key = keyStr.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");

        }
        catch (NoSuchAlgorithmException e) {
            log.error("[setKey] {}",e.getMessage());
            throw new InternalErrorException("cannot set secret key in encryptor");
        }
    }


    @Override
    public String encode(String toEncrypt) {
        try
        {
            if(secretKey==null)
                setKey();

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(toEncrypt.getBytes(StandardCharsets.UTF_8)));
        }
        catch (Exception e)
        {
            log.error("Error while encrypting: " + e.getMessage());
            throw new InternalErrorException("could not encrypt: "+toEncrypt);
        }
    }

    @Override
    public String decode(String toDecrypt){
        try
        {
            if(secretKey==null)
                setKey();

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(toDecrypt)));
        }
        catch (Exception e)
        {
           log.error("Error while decrypting: " + e.getMessage());
           throw new InternalErrorException("could not decrypt: "+toDecrypt);
        }
    }
}
