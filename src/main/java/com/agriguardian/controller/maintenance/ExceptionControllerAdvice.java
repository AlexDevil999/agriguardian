package com.agriguardian.controller.maintenance;

import com.agriguardian.exception.BadRequestException;
import com.agriguardian.exception.ConflictException;
import com.agriguardian.exception.InternalErrorException;
import com.agriguardian.exception.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Collections;

@Log4j2
@ControllerAdvice
public class ExceptionControllerAdvice {
    private static final String ERROR = "error";


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException e) {
        log.warn("[handleBadRequest]: response 'Bad Request'; rsn: " + e);
        return ResponseEntity.badRequest().body(Collections.singletonMap(ERROR, e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException e) {
        log.warn("[handleNotFound]: response 'Not Found'; rsn: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap(ERROR, e.getMessage()));
    }

    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<?> handleInternalErrorException(InternalErrorException e) {
        log.warn("[handleInternalErrorException]: response 'Internal Error'; rsn: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(ERROR, e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("[handleAccessDeniedException]: response 'FORBIDDEN'; rsn: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap(ERROR, e.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflictException(ConflictException e) {
        log.warn("[handleConflictException]: response 'CONFLICT'; rsn: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap(ERROR, e.getMessage()));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleThrowable(Throwable t) {
        log.error("[handleThrowable]: UNEXPECTED ERROR. Response 'Internal Error'; rsn: " + t);
        log.error("[handleThrowable]: msg: " + t.getMessage());
        t.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap(ERROR, t.getMessage()));
    }
}
