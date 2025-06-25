package com._data._data.game.controller;

import com._data._data.game.dto.QuizDto;
import com._data._data.game.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@Tag(name = "Quiz", description = "게이미피케이션 퀴즈 관련 API")
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/me/quiz")
    @Operation(
            summary = "퀴즈 정보 불러오기",
            description = "해당 ID의 퀴즈 정보를 반환합니다."
    )
    public QuizDto getQuizById(@RequestParam Long quizId) throws Exception {

        return quizService.getQuiz(quizId);
    }
}
