package com._data._data.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 회원가입 요청 DTO
 */
public record SigninRequest(
    String email,
    String name,
    Long nations,

    @Size(min = 8, message = "최소 8자 이상이어야 합니다.")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*\\W).+$",
        message = "대문자·숫자·특수문자를 포함해야 합니다."
    )
    String password
) {}
