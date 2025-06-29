package com._data._data.game.controller;

import com._data._data.game.dto.QuizListDto;
import com._data._data.game.dto.QuizRequestDto;
import com._data._data.game.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@Tag(name = "Quiz", description = "게이미피케이션 퀴즈 관련 API")
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/quiz")
    @Operation(
            summary = "퀴즈 정보 불러오기",
            description = "해당 level의 퀴즈 정보를 반환합니다."
    )
    public QuizListDto getQuizById(@RequestBody QuizRequestDto quizRequestDto) throws Exception {

        return quizService.getQuizzes(quizRequestDto);
    }

    @PatchMapping("/me/complete")
    @Operation(
            summary = "퀴즈 완료",
            description = "퀴즈를 완료하고 유저의 퀴즈 풀이 횟수 관련 정보를 업데이트합니다."
    )
    public void completeQuiz(@RequestParam Long quizId) {
        quizService.quizComplete(quizId);
    }
}
