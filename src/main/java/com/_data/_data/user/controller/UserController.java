package com._data._data.user.controller;

import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.common.dto.ApiResponse;
import com._data._data.user.dto.SigninRequest;
import com._data._data.user.entity.Users;
import com._data._data.user.exception.EmailAlreadyRegisteredException;
import com._data._data.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @Operation(
        summary     = "회원가입",
        description = "이메일, 비밀번호 등을 포함한 회원가입 정보를 전달하여 신규 사용자를 등록합니다."
    )
    @PostMapping("/api/user/register")
    public ApiResponse registerUser(@Valid @RequestBody SigninRequest req) {
        Users created = userService.register(req);
        return new ApiResponse(
            true,
            "회원가입이 완료되었습니다. (userId=" + created.getId() + ")"
        );
    }

    @Operation(
        summary     = "회원 탈퇴",
        description = "회원 ID를 전달하여 해당 사용자를 탈퇴 처리합니다."
    )
    @DeleteMapping("/api/user/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse deleteUser(@PathVariable("id") Long userId) {
        userService.deleteUser(userId);
        return new ApiResponse(
            true,
            "회원 탈퇴가 완료되었습니다."
        );
    }

    @Operation(
        summary     = "현재 로그인 중인 회원 탈퇴",
        description = "현재 로그인 중인 사용자를 탈퇴 처리합니다."
    )
    @DeleteMapping("/api/user/me")
    public ApiResponse deleteMyAccount(Authentication auth) {
        Long userId = ((CustomUserDetails) auth.getPrincipal()).getUser().getId();
        userService.deleteUser(userId);

        // 서버 측 SecurityContext 초기화
        SecurityContextHolder.clearContext();

        // 클라이언트에 명시적으로 알리기
        return new ApiResponse(true, "탈퇴되었습니다. 토큰을 삭제해주세요.");
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse handleEmailConflict(EmailAlreadyRegisteredException ex) {
        return new ApiResponse(false, ex.getMessage());
    }
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse handleNotFound(EntityNotFoundException ex) {
        return new ApiResponse(false, ex.getMessage());
    }
}
