package com._data._data.auth.controller;

import com._data._data.auth.dto.MailAuthRequest;
import com._data._data.auth.dto.MailSendRequest;
import com._data._data.common.dto.ApiResponse;
import com._data._data.user.exception.EmailAlreadyRegisteredException;
import com._data._data.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {

    private final UserService userService;

    @Operation(summary = "이메일 인증 코드 발송",
        description = "회원 가입 전, 입력된 이메일로 인증 코드를 발송합니다.")
    @PostMapping("/send")
    public ApiResponse sendAuthCode(
        @RequestBody MailSendRequest request
    ) throws MessagingException {
        boolean isSent = userService.sendAuthcode(request.email());
        return new ApiResponse(
            isSent,
            isSent ? "이메일로 인증 코드가 전송되었습니다." : "이메일 인증 코드 발급에 실패하였습니다."
        );
    }

    @Operation(summary = "인증 코드 검증",
        description = "전송된 인증 코드를 입력하여 이메일 검증을 수행합니다.")
    @PostMapping("/verify")
    public ApiResponse validateAuthCode(
        @RequestBody MailAuthRequest request
    ) {
        boolean isSuccess = userService.validationAuthcode(
            request.email(),
            request.verificationCode()
        );
        return new ApiResponse(
            isSuccess,
            isSuccess ? "이메일 인증에 성공하였습니다."
                : "이메일 인증에 실패하였습니다."
        );
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleAlreadyRegistered(EmailAlreadyRegisteredException ex) {
        return new ApiResponse(false, ex.getMessage());
    }
}
