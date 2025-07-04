UPDATE user_game_info 
SET total_quizzes_solved = 0 
WHERE total_quizzes_solved IS NULL;

UPDATE user_game_info 
SET quizzes_solved_today = 0 
WHERE quizzes_solved_today IS NULL;

UPDATE user_game_info 
SET chat_rooms_created = 0 
WHERE chat_rooms_created IS NULL;

UPDATE user_game_info 
SET level_completed = 0 
WHERE level_completed IS NULL;

UPDATE user_game_info 
SET current_count_in_level = 0 
WHERE current_count_in_level IS NULL;
