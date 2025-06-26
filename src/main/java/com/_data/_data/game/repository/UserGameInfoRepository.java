package com._data._data.game.repository;

import com._data._data.game.entity.UserGameInfo;
import com._data._data.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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

    void deleteByUser(Users user);
}
