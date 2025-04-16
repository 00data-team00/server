package com._data._data.aichat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomDto {

    private Long userId;

    private Long topicId;

    private String title;

    private String description;
}
