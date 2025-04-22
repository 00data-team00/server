-- src/main/resources/dummy-data.sql

-- 1) 한국(410), 미국(840), 일본(392), 중국(156), 캐나다(124) 등 ISO 국가번호 사용 예시
INSERT INTO users (email, nations) VALUES ('alice.kim@example.com', 410);
INSERT INTO users (email, nations) VALUES ('bob.lee@example.com',   840);
INSERT INTO users (email, nations) VALUES ('charlie.yamamoto@example.jp', 392);
INSERT INTO users (email, nations) VALUES ('david.zhang@example.cn', 156);
INSERT INTO users (email, nations) VALUES ('emily.clark@example.ca', 124);

-- 2) 추가 더미 데이터
INSERT INTO users (email, nations) VALUES ('frank.park@example.com', 410);
INSERT INTO users (email, nations) VALUES ('grace.hwang@example.com', 410);
INSERT INTO users (email, nations) VALUES ('helen.choi@example.com', 840);
