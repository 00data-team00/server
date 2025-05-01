package com._data._data.aichat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TopicDto {

    private Long id;

    private String category;

    private String title;

    private String description;

    private String userRole;

    private String aiRole;
}
