package com.agriguardian.util;

public class ValidationString {

    public static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
