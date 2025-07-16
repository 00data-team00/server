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

    @Builder.Default
    private Long totalQuizzesSolved = 0L;

    @Builder.Default
    private Long quizzesSolvedToday = 0L;

    @Builder.Default
    private Long chatRoomsCreated = 0L;

    @Builder.Default
    private Long levelCompleted = 0L;

    @Builder.Default
    private Long currentCountInLevel = 0L;

    // 주간 퀴즈 풀이 여부 추적 (월요일=0, 화요일=1, ..., 일요일=6)
    @Builder.Default
    @Column(name = "monday_solved", nullable = false)
    private Boolean mondaySolved = false;

    @Builder.Default
    @Column(name = "tuesday_solved", nullable = false)
    private Boolean tuesdaySolved = false;

    @Builder.Default
    @Column(name = "wednesday_solved", nullable = false)
    private Boolean wednesdaySolved = false;

    @Builder.Default
    @Column(name = "thursday_solved", nullable = false)
    private Boolean thursdaySolved = false;

    @Builder.Default
    @Column(name = "friday_solved", nullable = false)
    private Boolean fridaySolved = false;

    @Builder.Default
    @Column(name = "saturday_solved", nullable = false)
    private Boolean saturdaySolved = false;

    @Builder.Default
    @Column(name = "sunday_solved", nullable = false)
    private Boolean sundaySolved = false;

    // 주간 퀴즈 풀이 여부 업데이트 메서드
    public void updateWeeklyQuizStatus(int dayOfWeek, boolean solved) {
        switch (dayOfWeek) {
            case 1: // 월요일
                this.mondaySolved = solved;
                break;
            case 2: // 화요일
                this.tuesdaySolved = solved;
                break;
            case 3: // 수요일
                this.wednesdaySolved = solved;
                break;
            case 4: // 목요일
                this.thursdaySolved = solved;
                break;
            case 5: // 금요일
                this.fridaySolved = solved;
                break;
            case 6: // 토요일
                this.saturdaySolved = solved;
                break;
            case 7: // 일요일
                this.sundaySolved = solved;
                break;
        }
    }

    // 주간 퀴즈 풀이 여부 초기화 메서드
    public void resetWeeklyQuizStatus() {
        this.mondaySolved = false;
        this.tuesdaySolved = false;
        this.wednesdaySolved = false;
        this.thursdaySolved = false;
        this.fridaySolved = false;
        this.saturdaySolved = false;
        this.sundaySolved = false;
    }

    // 특정 요일의 퀴즈 풀이 여부 확인 메서드
    public boolean isQuizSolvedOnDay(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1: return this.mondaySolved;
            case 2: return this.tuesdaySolved;
            case 3: return this.wednesdaySolved;
            case 4: return this.thursdaySolved;
            case 5: return this.fridaySolved;
            case 6: return this.saturdaySolved;
            case 7: return this.sundaySolved;
            default: return false;
        }
    }

    // 추후 추가 정보
    // private Integer currentStreak;   // 연속 학습 횟수
    // private Integer level;
    // private LocalDate lastQuizDate;
}
