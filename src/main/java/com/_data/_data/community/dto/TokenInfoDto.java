package com._data._data.community.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenInfoDto {
    private Long contentId;
    private String contentType; // "POST" or "PROFILE"
}