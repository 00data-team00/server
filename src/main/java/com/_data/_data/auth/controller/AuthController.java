package com._data._data.auth.controller;

import com._data._data.auth.dto.LoginRequest;
import com._data._data.auth.dto.LoginResponse;
import com._data._data.auth.service.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        log.info("로그인 요청 수신: {}", request.email());
        try {
            LoginResponse res = this.authServiceImpl.login(request);
            log.info("로그인 성공: {}", request.email());
            return res;
        } catch (Exception e) {
            log.error("로그인 실패: {}", e.getMessage(), e);
            throw e;
        }
    }
}
