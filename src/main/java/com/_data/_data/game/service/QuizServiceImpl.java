package com._data._data.game.service;

import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.game.dto.QuizDto;
import com._data._data.game.entity.Quiz;
import com._data._data.game.entity.Word;
import com._data._data.game.exception.QuizNotFoundException;
import com._data._data.game.repository.QuizRepository;
import com._data._data.user.entity.Users;
import com._data._data.user.repository.UserRepository;
import com.deepl.api.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    @Value("${deepl.auth.key}")
    private String authKey;

    private DeepLClient client;

    @PostConstruct
    public void init() {
        this.client = new DeepLClient(authKey);
    }

    private final Map<String, String> glossaryCache = new ConcurrentHashMap<>();

    @Override
    public QuizDto getQuiz(Long quizId) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Users user = userRepository.findByEmail(userDetails.getUsername());
        String targetLang = user.getTranslationLang();

        Quiz quiz = quizRepository.findQuizWithChoices(quizId).orElseThrow(() -> new QuizNotFoundException(quizId));

        QuizDto quizDto = new QuizDto();
        quizDto.setCategory(quiz.getCategory());
        quizDto.setChoices(quiz.getChoices());
        quizDto.setImage(quiz.getImage());
        quizDto.setVoice(quiz.getVoice());

        int answer = quiz.getAnswer();
        quizDto.setAnswer(answer);

        try {
            TextResult result = client.translateText(quiz.getQuizText(), "ko", targetLang);
            log.debug("DeepL API 호출 성공 - Quiz Text 번역 결과: {}", result.getText());
            quizDto.setQuizText(result.getText());

            Word answerWord = quiz.getChoices().get(answer - 1);
            result = client.translateText(answerWord.getDescription(), "ko", targetLang);
            log.debug("DeepL API 호출 성공 - Word Description 번역 결과: {}", result.getText());
            quizDto.setWordScript(result.getText());

            String placeholder = "###" + UUID.randomUUID() + "###";
            String processedScript = quiz.getAnswerScript()
                    .replace(answerWord.getWord(), placeholder);

            TextResult resultScript = client.translateText(processedScript, "ko", targetLang);
            log.debug("DeepL API 호출 성공 - Answer Script 번역 결과: {}", result.getText());
            String finalScript = resultScript.getText()
                    .replace(placeholder, answerWord.getWord());

            quizDto.setAnswerScript(finalScript);

            return quizDto;
        } catch (Exception e) {
            log.error("Quiz DeepL API 호출 실패 - error");
            throw e;
        }
    }
}
