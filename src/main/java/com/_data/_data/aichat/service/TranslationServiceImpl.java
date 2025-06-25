package com._data._data.aichat.service;

import com._data._data.aichat.entity.Message;
import com._data._data.aichat.entity.Translation;
import com._data._data.aichat.exception.MessageNotFoundException;
import com._data._data.aichat.repository.MessageRepository;
import com._data._data.aichat.repository.TranslationRepository;
import com._data._data.auth.entity.CustomUserDetails;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final TranslationRepository translationRepository;

    @Value("${deepl.auth.key}")
    private String authKey;

    private DeepLClient client;

    @PostConstruct
    public void init() {
        this.client = new DeepLClient(authKey);
    }

    @Override
    public Translation translateMessage(Long messageId, String messageText, String targetLang) throws Exception {

        try {
            TextResult result = client.translateText(messageText, "ko", targetLang);
            log.debug("DeepL API 호출 성공 - 번역 결과: {}", result.getText());

            Translation translation = Translation.builder()
                    .messageId(messageId)
                    .lang(targetLang)
                    .translatedText(result.getText())
                    .build();

            return translationRepository.save(translation);
        } catch (Exception e) {
            log.error("DeepL API 호출 실패 - messageId: {}, error: {}", messageId, e.getMessage());
            throw e;
        }
    }

    @Override
    public Translation getTranslation(Long messageId) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Users user = userRepository.findByEmailAndIsDeletedFalse(userDetails.getUsername());
        String targetLang = user.getTranslationLang();

        Message message = messageRepository.findById(messageId).orElseThrow(() -> new MessageNotFoundException(messageId));
        String messageText = message.getText();

        Translation translation = translationRepository.findTranslationByMessageIdAndLang(messageId, targetLang).orElse(null);

        if (translation == null) {
            translation = translateMessage(messageId, messageText, targetLang);
        }

        return translation;
    }

    @Override
    public String translateText(String text, String sourceLang, String targetLang) throws Exception {
        try {
            TextResult result = client.translateText(text, sourceLang, targetLang);
            log.debug("DeepL API 호출 성공 - {}→{} 번역: {}", sourceLang, targetLang, result.getText());
            return result.getText();
        } catch (Exception e) {
            log.error("DeepL API 호출 실패 ({}→{}) - error: {}", sourceLang, targetLang, e.getMessage());
            throw e;
        }
    }
}
