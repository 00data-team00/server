package com._data._data.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class QuizListDto {

    List<QuizDto> quizDtoList = new ArrayList<>();
}
