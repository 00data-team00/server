package com._data._data.aichat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomInitDto {

    private Long chatRoomId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private Long messageId;

    private String message;
}