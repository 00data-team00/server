package com._data._data.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserGameInfoResponse {

    private String userName;

    private Long totalQuizzesSolved;

    private Long quizzesSolvedToday;

    private Long chatRoomsCreated;

    private Long levelCompleted;

    private List<Boolean> weeklyQuizStatus;
}
