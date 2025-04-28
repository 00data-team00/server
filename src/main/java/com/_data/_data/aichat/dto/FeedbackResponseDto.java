package com._data._data.aichat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FeedbackResponseDto {

    private Long feedbackId;

    private String lang;

    private String text;
}
