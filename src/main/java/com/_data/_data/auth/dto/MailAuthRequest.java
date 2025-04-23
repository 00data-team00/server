package com._data._data.auth.dto;

public record MailAuthRequest(
    String email,
    String verificationCode
) {}