package com._data._data.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizRequestDto {

    private Long quizId;

    private String userLang;
}
