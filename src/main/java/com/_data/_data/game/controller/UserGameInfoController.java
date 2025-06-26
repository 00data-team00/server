package com._data._data.game.controller;

import com._data._data.game.dto.UserGameInfoResponse;
import com._data._data.game.service.UserGameInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me/user-game-info")
@RequiredArgsConstructor
@Tag(name = "UserGameInfo", description = "유저 학습 기록 관련 API")
public class UserGameInfoController {

    private final UserGameInfoService userGameInfoService;

    @GetMapping()
    @Operation(
            summary = "유저 학습 기록 불러오기",
            description = "해당 유저의 총 퀴즈 풀이 횟수, 오늘 퀴즈 풀이 횟수, 총 회화 연습 횟수를 반환합니다."
    )
    public UserGameInfoResponse getUserGameInfo() {
        return userGameInfoService.getUserGameInfo();
    }
}
