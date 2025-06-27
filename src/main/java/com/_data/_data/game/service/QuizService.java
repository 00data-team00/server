package com._data._data.game.service;

import com._data._data.game.dto.QuizDto;
import com._data._data.game.dto.QuizRequestDto;

public interface QuizService {
    QuizDto getQuiz(QuizRequestDto quizRequestDto) throws Exception;

    void quizComplete(Long quizId);
}
