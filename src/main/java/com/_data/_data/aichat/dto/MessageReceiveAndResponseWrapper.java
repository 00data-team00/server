package com._data._data.aichat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageReceiveAndResponseWrapper {

    MessageResponseDto userMessage;

    MessageResponseDto aiMessage;
}

