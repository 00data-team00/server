package com._data._data.game.service;

import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.game.dto.QuizDto;
import com._data._data.game.dto.QuizListDto;
import com._data._data.game.dto.QuizRequestDto;
import com._data._data.game.entity.Quiz;
import com._data._data.game.entity.Word;
import com._data._data.game.exception.QuizNotFoundException;
import com._data._data.game.repository.QuizRepository;
import com._data._data.game.repository.UserGameInfoRepository;
import com._data._data.user.entity.Users;
import com._data._data.user.repository.UserRepository;
import com.deepl.api.DeepLClient;
import com.deepl.api.TextResult;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final UserGameInfoRepository userGameInfoRepository;

    @Value("${deepl.auth.key}")
    private String authKey;

    private DeepLClient client;

    @PostConstruct
    public void init() {
        this.client = new DeepLClient(authKey);
    }

    @Override
    public QuizListDto getQuizzes(QuizRequestDto quizRequestDto) throws Exception {

        String targetLang = quizRequestDto.getUserLang();
        Long level = quizRequestDto.getLevel();

        List<Quiz> quizzes = quizRepository.findQuizzesByLevelWithChoices(level);

        if (quizzes.isEmpty()) {
            throw new QuizNotFoundException(level);     // id 대신 level 전달
        }

        List<QuizDto> dtoList = new ArrayList<>();

        for (Quiz quiz : quizzes) {
            QuizDto quizDto = new QuizDto();
            quizDto.setCategory(quiz.getCategory());
            quizDto.setChoices(quiz.getChoices());
            quizDto.setImage(quiz.getImage());
            quizDto.setVoice(quiz.getVoice());
            quizDto.setAnswer(quiz.getAnswer());

            try {
                // 1) 퀴즈 문장 번역
                TextResult result = client.translateText(quiz.getQuizText(), "ko", targetLang);
                quizDto.setQuizText(result.getText());

                // 2) 정답 단어 설명 번역
                Word answerWord = quiz.getChoices().get(quiz.getAnswer() - 1);
                result = client.translateText(answerWord.getDescription(), "ko", targetLang);
                quizDto.setWordScript(result.getText());

                // 3) 정답 스크립트 번역 (정답 단어 보존)
                String placeholder = "###" + UUID.randomUUID() + "###";
                String processed = quiz.getAnswerScript().replace(answerWord.getWord(), placeholder);
                TextResult scriptResult = client.translateText(processed, "ko", targetLang);
                String finalScript = scriptResult.getText().replace(placeholder, answerWord.getWord());
                quizDto.setAnswerScript(finalScript);

            } catch (Exception e) {
                log.error("DeepL API 호출 실패 - quiz id {}: {}", quiz.getId(), e.getMessage());
                throw e;
            }

            dtoList.add(quizDto);
        }

        QuizListDto listDto = new QuizListDto();
        listDto.setQuizDtoList(dtoList);
        return listDto;
    }

    @Override
    public void quizComplete(Long quizId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Users user = userRepository.findByEmailAndIsDeletedFalse(userDetails.getUsername());

        userGameInfoRepository.incrementQuizzesSolvedToday(user.getId());
        userGameInfoRepository.incrementTotalQuizzesSolved(user.getId());
    }
}
