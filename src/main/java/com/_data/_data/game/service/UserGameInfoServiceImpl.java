package com._data._data.game.service;

import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.game.dto.UserGameInfoResponse;
import com._data._data.game.entity.UserGameInfo;
import com._data._data.game.repository.UserGameInfoRepository;
import com._data._data.user.entity.Users;
import com._data._data.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGameInfoServiceImpl implements UserGameInfoService {

    private final UserGameInfoRepository userGameInfoRepository;
    private final UserRepository userRepository;

    @Override
    public UserGameInfoResponse getUserGameInfo() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Users user = userRepository.findByEmailAndIsDeletedFalse(userDetails.getUsername());

        UserGameInfo userGameInfo = userGameInfoRepository.findByUser(user);

        List<Boolean> weeklyStatus = Arrays.asList(
                userGameInfo.getMondaySolved(),
                userGameInfo.getTuesdaySolved(),
                userGameInfo.getWednesdaySolved(),
                userGameInfo.getThursdaySolved(),
                userGameInfo.getFridaySolved(),
                userGameInfo.getSaturdaySolved(),
                userGameInfo.getSundaySolved()
        );

        UserGameInfoResponse userGameInfoResponse = new UserGameInfoResponse();
        userGameInfoResponse.setUserName(user.getName());
        userGameInfoResponse.setChatRoomsCreated(userGameInfo.getChatRoomsCreated());
        userGameInfoResponse.setQuizzesSolvedToday(userGameInfo.getQuizzesSolvedToday());
        userGameInfoResponse.setTotalQuizzesSolved(userGameInfo.getTotalQuizzesSolved());
        userGameInfoResponse.setLevelCompleted(userGameInfo.getLevelCompleted());
        userGameInfoResponse.setWeeklyQuizStatus(weeklyStatus);

        return userGameInfoResponse;
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void resetDailyCounters() {
        userGameInfoRepository.resetDailyQuizzes();
        log.info("Reset daily quizzes counter done.");
    }

    @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul")
    public void resetWeeklyQuizStatus() {
        try {
            log.info("주간 퀴즈 풀이 여부 초기화 시작");
            userGameInfoRepository.resetAllWeeklyQuizStatus();
            log.info("주간 퀴즈 풀이 여부 초기화 완료");
        } catch (Exception e) {
            log.error("주간 퀴즈 풀이 여부 초기화 중 오류 발생", e);
        }
    }
}
