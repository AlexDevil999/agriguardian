package com.agriguardian.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ConflictException extends RuntimeException {

    public ConflictException(String msg) {
        super(msg);
    }
}
