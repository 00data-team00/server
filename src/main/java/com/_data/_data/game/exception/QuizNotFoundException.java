package com._data._data.game.exception;

import com._data._data.aichat.exception.NotFoundException;

public class QuizNotFoundException extends NotFoundException {

    public QuizNotFoundException(Long level) {
        super("Quiz not found with level: " + level);
    }

    public QuizNotFoundException(Long quizId, Boolean isId) { super("Quiz not found with id: " + quizId); }
}
