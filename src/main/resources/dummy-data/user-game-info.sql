-- 기존 데이터 완전 삭제 후 새 데이터 삽입
TRUNCATE TABLE user_game_info;

INSERT IGNORE INTO user_game_info (user_id, total_quizzes_solved, quizzes_solved_today, chat_rooms_created, level_completed, current_count_in_level)
VALUES
    (7, 0, 0, 1, 0, 0),
    (6, 0, 0, 0, 0, 0),
    (5, 0, 0, 0, 0, 0),
    (4, 0, 0, 2, 0, 0),
    (3, 0, 0, 0, 0, 0),
    (2, 0, 0, 0, 0, 0),
    (1, 0, 0, 11, 0, 0);