package com._data._data.common.dto;

/**
 * 공통 응답 형식
 */
public record ApiResponse(
    boolean success,
    String msg
) {}
