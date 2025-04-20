package com._data._data.aichat.service;

import com._data._data.aichat.dto.ChatRoomDto;
import com._data._data.aichat.entity.ChatRoom;
import com._data._data.aichat.entity.Topic;
import com._data._data.aichat.exception.TopicNotFoundException;
import com._data._data.aichat.repository.ChatRoomRepository;
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

    @Override
    public ChatRoom creatChatRoom(ChatRoomDto chatRoomDto) {

        Long topicId = chatRoomDto.getTopicId();
        Long userId = chatRoomDto.getUserId();

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
    public List<ChatRoom> getChatRooms() {
        return chatRoomRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
    }

}
