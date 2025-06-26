package com._data._data.game.service;

import com._data._data.game.dto.QuizDto;

public interface QuizService {
    QuizDto getQuiz(Long quizId) throws Exception;

    void quizComplete(Long quizId);
}
