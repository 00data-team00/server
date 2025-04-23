package com._data._data.eduinfo.scheduler;

import com._data._data.eduinfo.service.EduProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EduProgramScheduler {
    private final EduProgramService eduProgramService;

    @Scheduled(fixedRate = 6 * 60 * 60 * 1000) // 6시간마다 실행
    public void fetchEduPrograms() {
        eduProgramService.fetchAndSavePrograms(); // 핵심 로직은 서비스에 위임
    }

}
