UPDATE user_game_info
SET
    monday_solved    = COALESCE(monday_solved, false),
    tuesday_solved   = COALESCE(tuesday_solved, false),
    wednesday_solved = COALESCE(wednesday_solved, false),
    thursday_solved  = COALESCE(thursday_solved, false),
    friday_solved    = COALESCE(friday_solved, false),
    saturday_solved  = COALESCE(saturday_solved, false),
    sunday_solved    = COALESCE(sunday_solved, false)
WHERE
    monday_solved IS NULL OR
    tuesday_solved IS NULL OR
    wednesday_solved IS NULL OR
    thursday_solved IS NULL OR
    friday_solved IS NULL OR
    saturday_solved IS NULL OR
    sunday_solved IS NULL;
