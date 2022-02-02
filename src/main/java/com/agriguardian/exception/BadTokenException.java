package com.agriguardian.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BadTokenException extends RuntimeException {
    public BadTokenException(String msg) {
        super(msg);
    }
}
