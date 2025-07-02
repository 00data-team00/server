package com._data._data.game.repository;

import com._data._data.game.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    @Query("select distinct q from Quiz q join fetch q.choices where q.id = :id")
    Optional<Quiz> findQuizWithChoices(@Param("id") Long id);

    // ★ level에 속하는 모든 퀴즈 + choices 한번에 조회
    @Query("select distinct q from Quiz q join fetch q.choices where q.level = :level")
    List<Quiz> findQuizzesByLevelWithChoices(@Param("level") Long level);

    Long getCountByLevel(Long level);
}
