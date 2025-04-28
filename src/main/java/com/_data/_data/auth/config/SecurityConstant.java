package com._data._data.auth.config;

public class SecurityConstant {
    public static final String[] WHITE_LIST = {
        "/api/user/register",
        "/api/login",
        "/v3/api-docs/**",
        "/swagger-ui.html",
        "/swagger-ui/**"
    };
}
