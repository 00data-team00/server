package com._data._data.game.repository;

import com._data._data.game.entity.UserGameInfo;
import com._data._data.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserGameInfoRepository extends JpaRepository<UserGameInfo, Long> {
    UserGameInfo findByUser(Users user);

    @Transactional
    @Modifying
    @Query("UPDATE UserGameInfo u SET u.quizzesSolvedToday = 0")
    void resetDailyQuizzes();

    @Transactional
    @Modifying
    @Query("UPDATE UserGameInfo u SET u.quizzesSolvedToday = u.quizzesSolvedToday + 1 WHERE u.user.id = :userId")
    void incrementQuizzesSolvedToday(Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE UserGameInfo u SET u.totalQuizzesSolved = u.totalQuizzesSolved + 1 WHERE u.user.id = :userId")
    void incrementTotalQuizzesSolved(Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE UserGameInfo u SET u.chatRoomsCreated = u.chatRoomsCreated + 1 WHERE u.user.id = :userId")
    void incrementChatRoomsCreated(Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE UserGameInfo u SET u.currentCountInLevel = 0 WHERE u.user.id = :userId")
    void resetCurrentCountInLevel(Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE UserGameInfo u SET u.currentCountInLevel = u.currentCountInLevel + 1 WHERE u.user.id = :userId")
    void incrementCurrentCountInLevel(Long userId);

    @Query("SELECT u.currentCountInLevel FROM UserGameInfo u WHERE u.user.id = :userId")
    Long getCurrentCountInLevelByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE UserGameInfo u SET u.levelCompleted = :level WHERE u.user.id = :userId")
    void updateLevelCompleted(Long userId, Long level);

    void deleteByUser(Users user);

    // 주간 퀴즈 풀이 여부 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE UserGameInfo u SET " +
            "u.mondaySolved = CASE WHEN :dayOfWeek = 1 THEN true ELSE u.mondaySolved END, " +
            "u.tuesdaySolved = CASE WHEN :dayOfWeek = 2 THEN true ELSE u.tuesdaySolved END, " +
            "u.wednesdaySolved = CASE WHEN :dayOfWeek = 3 THEN true ELSE u.wednesdaySolved END, " +
            "u.thursdaySolved = CASE WHEN :dayOfWeek = 4 THEN true ELSE u.thursdaySolved END, " +
            "u.fridaySolved = CASE WHEN :dayOfWeek = 5 THEN true ELSE u.fridaySolved END, " +
            "u.saturdaySolved = CASE WHEN :dayOfWeek = 6 THEN true ELSE u.saturdaySolved END, " +
            "u.sundaySolved = CASE WHEN :dayOfWeek = 7 THEN true ELSE u.sundaySolved END " +
            "WHERE u.user.id = :userId")
    void updateWeeklyQuizStatus(@Param("userId") Long userId, @Param("dayOfWeek") int dayOfWeek);

    // 주간 퀴즈 풀이 여부 초기화 (모든 사용자)
    @Modifying
    @Transactional
    @Query("UPDATE UserGameInfo u SET " +
            "u.mondaySolved = false, " +
            "u.tuesdaySolved = false, " +
            "u.wednesdaySolved = false, " +
            "u.thursdaySolved = false, " +
            "u.fridaySolved = false, " +
            "u.saturdaySolved = false, " +
            "u.sundaySolved = false")
    void resetAllWeeklyQuizStatus();
}
