package com._data._data.game.controller;

import com._data._data.game.dto.QuizDto;
import com._data._data.game.entity.Quiz;
import com._data._data.game.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/me/quiz")
    public QuizDto getQuizById(@RequestParam Long id) {
        Quiz quiz = quizService.getQuiz(id);

        QuizDto quizDto = new QuizDto();
        quizDto.setCategory(quiz.getCategory());
        quizDto.setQuizText(quiz.getQuizText());
        quizDto.setChoices(quiz.getChoices());
        quizDto.setImage(quiz.getImage());
        quizDto.setVoice(quiz.getVoice());
        quizDto.setAnswer(quiz.getAnswer());
        quizDto.setAnswerScript(quiz.getAnswerScript());

        return quizDto;
    }
}
