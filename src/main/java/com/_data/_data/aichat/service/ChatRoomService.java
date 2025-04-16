package com._data._data.aichat.service;

import com._data._data.aichat.entity.ChatRoom;
import com._data._data.aichat.entity.Message;

import java.util.List;

public interface ChatRoomService {
    ChatRoom creatChatRoom(Long topicId, Long userId);

    Message receiveMessage(Long chatRoomId, String text);

    Message generateAiMessage(Long messageId);

    List<ChatRoom> getChatRooms();

    List<Message> getMessages(Long chatRoomId);
}
