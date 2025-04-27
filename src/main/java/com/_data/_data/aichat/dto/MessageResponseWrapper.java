package com._data._data.aichat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MessageResponseWrapper {
    private List<MessageResponseDto> messages;
}
