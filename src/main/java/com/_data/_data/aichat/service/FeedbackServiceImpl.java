package com._data._data.aichat.service;

import com._data._data.aichat.entity.Feedback;
import com._data._data.aichat.entity.Message;
import com._data._data.aichat.exception.MessageNotFoundException;
import com._data._data.aichat.repository.FeedbackRepository;
import com._data._data.aichat.repository.MessageRepository;
import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.user.entity.Users;
import com._data._data.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class FeedbackServiceImpl implements FeedbackService{

    private final FeedbackRepository feedbackRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private final ChatClient chatClient;

    public FeedbackServiceImpl(
            FeedbackRepository feedbackRepository,
            MessageRepository messageRepository,
            UserRepository userRepository,
            ChatClient.Builder chatClientBuilder)
    {
        this.feedbackRepository = feedbackRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatClient = chatClientBuilder.build();
    }

    @Value("classpath:templates/chat-feedback-prompt.st")
    private Resource chatFeedbackPromptResource;

    @Override
    public Feedback generateFeedback(Long messageId, String messageText, String lang) {
        PromptTemplate promptTemplate = new PromptTemplate(chatFeedbackPromptResource);

        Map<String, Object> params = Map.of(
                "message", messageText,
                "language", lang
        );

        String prompt = promptTemplate.render(params);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content()
                .replaceAll("^\"|\"$", "");

        log.info("response: {}", response);

        Feedback feedback = Feedback.builder()
                .messageId(messageId)
                .lang(lang)
                .feedbackText(response)
                .build();

        return feedbackRepository.save(feedback);
    }

    @Override
    public Feedback getFeedback(Long messageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Users user = userRepository.findByEmail(userDetails.getUsername());
        String targetLang = user.getTranslationLang();

        Message message = messageRepository.findById(messageId).orElseThrow(() -> new MessageNotFoundException(messageId));
        String messageText = message.getText();

        Feedback feedback = feedbackRepository.findFeedbackByMessageIdAndLang(messageId, targetLang).orElse(null);

        if (feedback == null) {
            feedback = generateFeedback(messageId, messageText, targetLang);
        }

        return feedback;
    }
}
