package com._data._data.user.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException(String email) {
        super("이미 가입된 이메일입니다: " + email);
    }
}