package com._data._data.auth.exception;

import com._data._data.common.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmailNotFoundException extends NotFoundException {
    public EmailNotFoundException(String email) {
        super("등록되지 않은 이메일입니다: " + email);
    }
}