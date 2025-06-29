package com._data._data.game.service;

import com._data._data.game.dto.QuizListDto;
import com._data._data.game.dto.QuizRequestDto;

public interface QuizService {
    QuizListDto getQuizzes(QuizRequestDto quizRequestDto) throws Exception;

    void quizComplete(Long quizId);
}
