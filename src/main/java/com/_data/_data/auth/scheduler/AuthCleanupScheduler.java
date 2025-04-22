package com._data._data.auth.scheduler;

import com._data._data.auth.repository.AuthRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCleanupScheduler {
    private final AuthRepository authRepository;

    /**
     * 매일 자정(00:00)에 만료된 인증코드 삭제
     * cron 형식: 초 분 시 일 월 요일
     * zone은 서버 타임존에 맞추세요(예: Asia/Seoul)
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void purgeExpiredAuthCodes() {
        long deleted = authRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        if (deleted > 0) {
            log.info("만료된 인증 코드 {}건 삭제", deleted);
        }
    }
}
