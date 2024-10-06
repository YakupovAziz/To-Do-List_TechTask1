package com.todo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ForbiddenDate extends RuntimeException {
    public ForbiddenDate(String message) {
        super(message);
    }
}
