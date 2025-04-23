package com._data._data.auth.dto;

public record LoginResponse(
    String accessToken,
    String refreshToken
) {}