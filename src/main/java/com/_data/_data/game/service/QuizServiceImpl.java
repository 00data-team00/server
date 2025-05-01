package com._data._data.game.service;

import com._data._data.game.entity.Quiz;
import com._data._data.game.exception.QuizNotFoundException;
import com._data._data.game.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;

    @Override
    public Quiz getQuiz(Long quizId) {
        return quizRepository.findQuizWithChoices(quizId).orElseThrow(() -> new QuizNotFoundException(quizId));
    }
}
