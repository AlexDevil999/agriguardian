package com.agriguardian.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserFromTokenDoesNotExistsException extends RuntimeException {
    public UserFromTokenDoesNotExistsException(String msg) {
        super(msg);
    }

}
