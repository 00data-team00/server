package com._data._data.user.controller;

import com._data._data.common.dto.ApiResponse;
import com._data._data.user.dto.SigninRequest;
import com._data._data.user.entity.Users;
import com._data._data.user.exception.EmailAlreadyRegisteredException;
import com._data._data.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/api/user/register")
    public ApiResponse registerUser(@Valid @RequestBody SigninRequest req) {
        Users created = userService.register(req);
        return new ApiResponse(
            true,
            "회원가입이 완료되었습니다. (userId=" + created.getId() + ")"
        );
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse handleEmailConflict(EmailAlreadyRegisteredException ex) {
        return new ApiResponse(false, ex.getMessage());
    }
}
