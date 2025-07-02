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

        UserGameInfoResponse userGameInfoResponse = new UserGameInfoResponse();
        userGameInfoResponse.setUserName(user.getName());
        userGameInfoResponse.setChatRoomsCreated(userGameInfo.getChatRoomsCreated());
        userGameInfoResponse.setQuizzesSolvedToday(userGameInfo.getQuizzesSolvedToday());
        userGameInfoResponse.setTotalQuizzesSolved(userGameInfo.getTotalQuizzesSolved());
        userGameInfoResponse.setLevelCompleted(userGameInfo.getLevelCompleted());

        return userGameInfoResponse;
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void resetDailyCounters() {
        userGameInfoRepository.resetDailyQuizzes();
        log.info("Reset daily quizzes counter done.");
    }
}
