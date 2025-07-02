package com._data._data.game.entity;

import com._data._data.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_game_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGameInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    private Long totalQuizzesSolved   = 0L;

    private Long quizzesSolvedToday   = 0L;

    private Long chatRoomsCreated     = 0L;

    private Long levelCompleted = 0L;

    private Long currentCountInLevel = 0L;

    // 추후 추가 정보
//    private Integer currentStreak;   // 연속 학습 횟수
//    private Integer level;
//    private LocalDate lastQuizDate;
}

