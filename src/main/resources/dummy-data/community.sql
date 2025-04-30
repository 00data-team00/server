-- =========================
-- 1. users 테이블 데이터 삽입
-- =========================
INSERT INTO users (email, name, nations, translation_lang, password, profile_image)
VALUES
('user1@example.com',  'User1', 1, 'en-US', '$2a$10$2GDRotpWIt3/Q93sdroB8eL8yV4XhqpBtwx.nPF0u.4H6DQUvPqsu', '/uploads/profile/blank.jpg'),
('user2@example.com',  'User2', 1, 'en-US', '$2a$10$2GDRotpWIt3/Q93sdroB8eL8yV4XhqpBtwx.nPF0u.4H6DQUvPqsu', '/uploads/profile/blank.jpg'),
('user3@example.com',  'User3', 1, 'en-US', '$2a$10$2GDRotpWIt3/Q93sdroB8eL8yV4XhqpBtwx.nPF0u.4H6DQUvPqsu', '/uploads/profile/blank.jpg'),
('user4@example.com',  'User4', 1, 'en-US', '$2a$10$2GDRotpWIt3/Q93sdroB8eL8yV4XhqpBtwx.nPF0u.4H6DQUvPqsu', '/uploads/profile/blank.jpg'),
('user5@example.com',  'User5', 1, 'en-US', '$2a$10$2GDRotpWIt3/Q93sdroB8eL8yV4XhqpBtwx.nPF0u.4H6DQUvPqsu', '/uploads/profile/blank.jpg'),
('user6@example.com',  'User6', 1, 'en-US', '$2a$10$2GDRotpWIt3/Q93sdroB8eL8yV4XhqpBtwx.nPF0u.4H6DQUvPqsu', '/uploads/profile/blank.jpg'),
('user7@example.com',  'User7', 2, 'jp', '$2a$10$2GDRotpWIt3/Q93sdroB8eL8yV4XhqpBtwx.nPF0u.4H6DQUvPqsu', '/uploads/profile/blank.jpg'),
('user8@example.com',  'User8', 2, 'jp', '$2a$10$2GDRotpWIt3/Q93sdroB8eL8yV4XhqpBtwx.nPF0u.4H6DQUvPqsu', '/uploads/profile/blank.jpg'),
('user9@example.com',  'User9', 2, 'jp', '$2a$10$2GDRotpWIt3/Q93sdroB8eL8yV4XhqpBtwx.nPF0u.4H6DQUvPqsu', '/uploads/profile/blank.jpg'),
('user10@example.com', 'User10',2, 'jp', '$2a$10$2GDRotpWIt3/Q93sdroB8eL8yV4XhqpBtwx.nPF0u.4H6DQUvPqsu', '/uploads/profile/blank.jpg'),
('user11@example.com', 'User11',2, 'jp', '$2a$10$2GDRotpWIt3/Q93sdroB8eL8yV4XhqpBtwx.nPF0u.4H6DQUvPqsu', '/uploads/profile/blank.jpg'),
('user12@example.com', 'User12',2, 'jp', '$2a$10$2GDRotpWIt3/Q93sdroB8eL8yV4XhqpBtwx.nPF0u.4H6DQUvPqsu', '/uploads/profile/blank.jpg')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  nations = VALUES(nations),
  translation_lang = VALUES(translation_lang),
  password = VALUES(password),
  profile_image = VALUES(profile_image);

-- =========================
-- 2. posts 테이블 데이터 삽입
-- =========================
INSERT INTO post (id, user_id, image_url, content, created_at, like_count, comment_count)
VALUES
(1, 1, NULL, 'User1이 작성한 첫 번째 게시글입니다.', NOW(), 2, 2),
(2, 1, NULL, 'User1이 작성한 두 번째 게시글입니다.', NOW(), 1, 1),
(3, 2, NULL, 'User2가 작성한 게시글입니다.', NOW(), 1, 1),
(4, 3, NULL, 'User3이 작성한 게시글입니다.', NOW(), 1, 1),
(5, 4, NULL, 'User4가 작성한 게시글입니다.', NOW(), 1, 1),
(6, 5, NULL, 'User5가 작성한 게시글입니다.', NOW(), 1, 1),
(7, 6, NULL, 'User6이 작성한 게시글입니다.', NOW(), 1, 1),
(8, 7, NULL, 'User7이 작성한 게시글입니다.', NOW(), 1, 1),
(9, 8, NULL, 'User8이 작성한 게시글입니다.', NOW(), 1, 1),
(10, 9, NULL, 'User9가 작성한 게시글입니다.', NOW(), 1, 1),
(11, 10, NULL, 'User10이 작성한 게시글입니다.', NOW(), 1, 0),
(12, 11, NULL, 'User11이 작성한 게시글입니다.', NOW(), 1, 0),
(13, 12, NULL, 'User12가 작성한 게시글입니다.', NOW(), 1, 0),
(14, 7, NULL, 'User7이 작성한 두 번째 게시글입니다.', NOW(), 1, 0),
(15, 8, NULL, 'User8이 작성한 두 번째 게시글입니다.', NOW(), 1, 0),
(16, 9, NULL, 'User9이 작성한 두 번째 게시글입니다.', NOW(), 0, 0)
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  image_url = VALUES(image_url),
  content = VALUES(content),
  created_at = VALUES(created_at),
  like_count = VALUES(like_count),
  comment_count = VALUES(comment_count);

-- =========================
-- 3. comments 테이블 데이터 삽입
-- =========================
INSERT INTO comments (id, post_id, user_id, content, created_at)
VALUES
(1, 1, 2, 'User2가 User1 게시글에 댓글 작성', NOW()),
(2, 1, 3, 'User3이 User1 게시글에 댓글 작성', NOW()),
(3, 2, 4, 'User4가 User1 두 번째 게시글에 댓글 작성', NOW()),
(4, 3, 1, 'User1이 User2 게시글에 댓글 작성', NOW()),
(5, 4, 2, 'User2가 User3 게시글에 댓글 작성', NOW()),
(6, 5, 6, 'User6이 User4 게시글에 댓글 작성', NOW()),
(7, 6, 5, 'User5가 User5 본인 게시글에 댓글 작성', NOW()),
(8, 7, 1, 'User1이 User6 게시글에 댓글 작성', NOW()),
(9, 8, 9, 'User9가 User7 게시글에 댓글 작성', NOW()),
(10, 9, 10, 'User10이 User8 게시글에 댓글 작성', NOW())
ON DUPLICATE KEY UPDATE
  post_id = VALUES(post_id),
  user_id = VALUES(user_id),
  content = VALUES(content),
  created_at = VALUES(created_at);

-- =========================
-- 4. likes 테이블 데이터 삽입
-- =========================
INSERT INTO likes (id, post_id, user_id, created_at)
VALUES
(1, 1, 2, NOW()),
(2, 1, 3, NOW()),
(3, 2, 4, NOW()),
(4, 3, 1, NOW()),
(5, 4, 5, NOW()),
(6, 5, 6, NOW()),
(7, 6, 1, NOW()),
(8, 7, 2, NOW()),
(9, 8, 3, NOW()),
(10, 9, 4, NOW()),
(11, 10, 5, NOW()),
(12, 11, 6, NOW()),
(13, 12, 7, NOW()),
(14, 13, 8, NOW()),
(15, 14, 9, NOW()),
(16, 15, 10, NOW())
ON DUPLICATE KEY UPDATE
  post_id = VALUES(post_id),
  user_id = VALUES(user_id),
  created_at = VALUES(created_at);

-- =========================
-- 5. follows 테이블 데이터 삽입
-- =========================
INSERT INTO follows (id, follower_id, followee_id, created_at)
VALUES
(1, 1, 2, NOW()),
(2, 2, 3, NOW()),
(3, 3, 1, NOW()),
(4, 4, 5, NOW()),
(5, 5, 6, NOW()),
(6, 6, 1, NOW()),
(7, 7, 8, NOW()),
(8, 8, 9, NOW()),
(9, 9, 7, NOW()),
(10, 10, 11, NOW()),
(11, 11, 12, NOW()),
(12, 12, 10, NOW())
ON DUPLICATE KEY UPDATE
  follower_id = VALUES(follower_id),
  followee_id = VALUES(followee_id),
  created_at = VALUES(created_at);
