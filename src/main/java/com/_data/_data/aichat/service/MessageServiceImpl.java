package com._data._data.aichat.service;

import com._data._data.aichat.entity.Message;
import com._data._data.aichat.entity.Topic;
import com._data._data.aichat.exception.ChatRoomNotFoundException;
import com._data._data.aichat.exception.TopicNotFoundException;
import com._data._data.aichat.repository.ChatRoomRepository;
import com._data._data.aichat.repository.MessageRepository;
import com._data._data.aichat.repository.TopicRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final TopicRepository topicRepository;

    private final ChatClient chatClient;

    public MessageServiceImpl(
            MessageRepository messageRepository,
            ChatRoomRepository chatRoomRepository,
            TopicRepository topicRepository,
            ChatClient.Builder chatClientBuilder)
    {
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.topicRepository = topicRepository;
        this.chatClient = chatClientBuilder.build();
    }

    @Value("classpath:templates/chat-start-prompt.st")
    private Resource promptResource;

    @Override
    public Message receiveMessage(Long chatRoomId, String text) {
        Message message = Message.builder()
                .chatRoomId(chatRoomId)
                .text(text)
                .isUser(true)
                .build();

        return messageRepository.save(message);
    }

    @Override
    public Message generateBeginningMessage(Long topicId, Long chatRoomId) {
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new TopicNotFoundException(topicId));

        PromptTemplate promptTemplate = new PromptTemplate(promptResource);

        Map<String, Object> params = Map.of(
                "situation", topic.getDescription(),
                "role", topic.getAiRole()
        );

        String prompt = promptTemplate.render(params);

        String response = chatClient.prompt().user(prompt).call().content();

        log.info("response: {}", response);

        Message message = Message.builder()
                .chatRoomId(chatRoomId)
                .text(response)
                .isUser(false)
                .build();

        return messageRepository.save(message);
    }

    @Override
    public Message generateAiMessage(Long chatRoomId) {
        List<Message> messages = getAllMessages(chatRoomId);

        return null;
    }

    @Override
    public List<Message> getAllMessages(Long chatRoomId) {
        chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ChatRoomNotFoundException(chatRoomId));
        return messageRepository.findByChatRoomIdOrderByStoredAt(chatRoomId);
    }
}
