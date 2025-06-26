-- user_game_info 더미 데이터 삽입 예시

INSERT INTO user_game_info (user_id, total_quizzes_solved, quizzes_solved_today, chat_rooms_created) VALUES
(7, 0, 0, 0),
(6, 0, 0, 0),
(5, 0, 0, 0),
(4, 0, 0, 2),
(3, 0, 0, 0),
(2, 0, 0, 0),
(1, 0, 0, 5);
ON DUPLICATE KEY UPDATE
total_quizzes_solved = VALUES(total_quizzes_solved),
quizzes_solved_today = VALUES(quizzes_solved_today),
chat_rooms_created = VALUES(chat_rooms_created);