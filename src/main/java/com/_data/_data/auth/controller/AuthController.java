package com._data._data.auth.controller;

import com._data._data.auth.dto.LoginRequest;
import com._data._data.auth.dto.LoginResponse;
import com._data._data.auth.exception.EmailNotFoundException;
import com._data._data.auth.exception.InvalidPasswordException;
import com._data._data.auth.service.AuthServiceImpl;
import com._data._data.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("로그인 요청: {}", request.email());
        try {
            LoginResponse tokens = authServiceImpl.login(request);
            log.info("로그인 성공: {}", request.email());
            return ResponseEntity.ok(tokens);

        } catch (EmailNotFoundException ex) {
            log.warn("로그인 실패 – 사용자 없음: {}", request.email());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, ex.getMessage()));

        } catch (InvalidPasswordException ex) {
            log.warn("로그인 실패 – 비밀번호 불일치: {}", request.email());
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, ex.getMessage()));
        }
    }
}
