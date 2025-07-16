package com._data._data.community.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShareResponse {
    private String shareUrl;
    private String token;
}