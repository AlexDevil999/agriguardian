package com.agriguardian.exception;

public class UserFormTokenDoesNotExistsException extends RuntimeException {
    public UserFormTokenDoesNotExistsException(String msg) {
        super(msg);
    }
}
