package com._data._data.auth.exception;

import com._data._data.common.exception.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidPasswordException extends BadRequestException {
    public InvalidPasswordException() {
        super("비밀번호가 일치하지 않습니다.");
    }
}