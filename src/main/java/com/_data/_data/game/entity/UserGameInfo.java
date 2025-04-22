package com._data._data.game.entity;

import com._data._data.user.entity.Users;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGameInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    private Long totalQuizzesSolved   = 0L;

    private Long quizzesSolvedToday   = 0L;

    private Long chatRoomsCreated     = 0L;

    // 추후 추가 정보
//    private Integer currentStreak;   // 연속 학습 횟수
//    private Integer level;
//    private LocalDate lastQuizDate;
}

