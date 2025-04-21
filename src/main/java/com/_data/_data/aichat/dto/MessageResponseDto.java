package com._data._data.aichat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MessageResponseDto extends MessageReceiveDto {

    private Long messageId;

    private String text;

    private Boolean isUser;

    private LocalDateTime storedAt;
}
