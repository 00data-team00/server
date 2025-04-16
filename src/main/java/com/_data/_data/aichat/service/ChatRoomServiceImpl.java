package com._data._data.aichat.service;

import com._data._data.aichat.entity.ChatRoom;
import com._data._data.aichat.entity.Message;
import com._data._data.aichat.entity.Topic;
import com._data._data.aichat.exception.MessageNotFoundException;
import com._data._data.aichat.exception.TopicNotFoundException;
import com._data._data.aichat.repository.ChatRoomRepository;
import com._data._data.aichat.repository.MessageRepository;
import com._data._data.aichat.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final TopicRepository topicRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    @Override
    public ChatRoom creatChatRoom(Long userId, Long topicId) {

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException(topicId));

        ChatRoom chatRoom = ChatRoom.builder()
                .userId(userId)
                .topicId(topicId)
                .title(topic.getTitle())
                .description(topic.getDescription())
                .isFinished(false)
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public Message receiveMessage(Long chatRoomId, String text) {
        Message message = Message.builder()
                .chatRoomId(chatRoomId)
                .text(text)
                .isUser(true)
                .build();

        return messageRepository.save(message);
    }

    //gpt-api 가져와서 구현
    @Override
    public Message generateAiMessage(Long messageId) {
        Message userMessage = messageRepository.findById(messageId).orElseThrow(() -> new MessageNotFoundException(messageId));

        return null;
    }

    @Override
    public List<ChatRoom> getChatRooms() {
        return chatRoomRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
    }

    @Override
    public List<Message> getMessages(Long chatRoomId) {
        return messageRepository.findByChatRoomIdOrderByStoredAt(chatRoomId);
    }

}
