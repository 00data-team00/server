package com._data._data.auth.controller;

import com._data._data.auth.dto.LoginRequest;
import com._data._data.auth.dto.LoginResponse;
import com._data._data.auth.dto.RefreshRequest;
import com._data._data.auth.exception.EmailNotFoundException;
import com._data._data.auth.exception.InvalidPasswordException;
import com._data._data.auth.exception.TokenExpiredException;
import com._data._data.auth.exception.TokenNotFoundException;
import com._data._data.auth.jwt.RefreshTokenService;
import com._data._data.auth.service.AuthServiceImpl;
import com._data._data.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Auth", description = "로그인 인증 관련 API")
public class AuthController {

    private final AuthServiceImpl authServiceImpl;
    private final RefreshTokenService refreshTokenService;

    @Operation(
        summary = "로그인",
        description = "사용자 이메일과 비밀번호로 인증을 수행하고, 성공 시 JWT 토큰을 반환합니다."
    )
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


    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새로운 Access Token을 발급받습니다.")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshRequest request) {
        try {
            LoginResponse tokens = refreshTokenService.refreshAccessToken(request.refreshToken());
            return ResponseEntity.ok(tokens);
        } catch (TokenNotFoundException | TokenExpiredException ex) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "Refresh Token을 무효화합니다.")
    public ResponseEntity<?> logout(@Valid @RequestBody RefreshRequest request) {
        refreshTokenService.logout(request.refreshToken());
        return ResponseEntity.ok(new ApiResponse(true, "로그아웃되었습니다."));
    }
}
