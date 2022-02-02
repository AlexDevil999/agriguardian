package com.agriguardian.util;

public class ValidationString {

    public static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isBlank(String str) {
        return !isNotBlank(str);
    }

}
