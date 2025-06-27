package com._data._data.aichat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TranslateRequestDto {

    private Long messageId;

    private String userLang;
}
