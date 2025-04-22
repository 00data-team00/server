-- src/main/resources/dummy-data.sql

-- 이미 존재하면 삽입을 무시하도록 INSERT IGNORE 사용
INSERT IGNORE INTO users (email, nations) VALUES
    ('alice.kim@example.com',               410),
    ('bob.lee@example.com',                 840),
    ('charlie.yamamoto@example.jp',         392),
    ('david.zhang@example.cn',              156),
    ('emily.clark@example.ca',              124),
    ('frank.park@example.com',              410),
    ('grace.hwang@example.com',             410),
    ('helen.choi@example.com',              840);
