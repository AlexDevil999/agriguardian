package com.agriguardian.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class RandomCodeGenerator {

    public static String generateConfirmationCode() {
        return generateRandomNumbers(4);
    }

    public static String generateTemporaryPassword() {
        return RandomStringUtils.randomAscii(8,14);
    }

    public static String generateInvitationCode() {
        return generateRandomNumbers(6);
    }

    public static String generateRandomNumbers(int length) {
        Random random = new Random(System.nanoTime());
        StringBuilder stringBuilder = new StringBuilder("");

        for (int i = 0; i < length; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }
}
