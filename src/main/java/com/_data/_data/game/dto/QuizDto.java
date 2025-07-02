package com._data._data.game.dto;

import com._data._data.game.entity.Word;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class QuizDto {

    private Long quizId;

    private String category;

    private String quizText;

    private List<Word> choices;

    private String image;

    private String voice;

    private int answer;

    private String wordScript;

    private String answerScript;
}
