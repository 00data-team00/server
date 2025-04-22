package com._data._data.auth.service;

import com._data._data.auth.dto.LoginRequest;
import com._data._data.auth.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}