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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final TranslationRepository translationRepository;

    private final String authKey = "96aa7506-ac4d-47e7-aedc-e954b3fbe273:fx";
    private final DeepLClient client = new DeepLClient(authKey);

    @Override
    public Translation translateMessage(Long messageId, String messageText, String targetLang) throws Exception {

        TextResult result = client.translateText(messageText, "ko", targetLang);

        Translation translation = Translation.builder()
                .messageId(messageId)
                .lang(targetLang)
                .translatedText(result.getText())
                .build();

        return translationRepository.save(translation);
    }

    @Override
    public Translation getTranslation(Long messageId) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Users user = userRepository.findByEmail(userDetails.getUsername());
        String targetLang = user.getTranslationLang();

        Message message = messageRepository.findById(messageId).orElseThrow(() -> new MessageNotFoundException(messageId));
        String messageText = message.getText();

        Translation translation = translationRepository.findTranslationByMessageIdAndLang(messageId, targetLang).orElse(null);

        if (translation == null) {
            translation = translateMessage(messageId, messageText, targetLang);
        }

        return translation;
    }
}
