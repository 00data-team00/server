package com._data._data.aichat.service;

import com._data._data.aichat.entity.ChatRoom;
import com._data._data.aichat.entity.Topic;
import com._data._data.aichat.exception.TopicNotFoundException;
import com._data._data.aichat.repository.ChatRoomRepository;
import com._data._data.aichat.repository.TopicRepository;
import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.user.entity.Users;
import com._data._data.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final TopicRepository topicRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Override
    public ChatRoom creatChatRoom(Long topicId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Users user = userRepository.findByEmail(userDetails.getUsername());

        Long userId = user.getId();

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Users user = userRepository.findByEmail(userDetails.getUsername());

        return chatRoomRepository.findByUserIdOrderByUpdatedAtDesc(user.getId());
    }

}
