package com._data._data.aichat.service;

import com._data._data.aichat.dto.ChatRoomDto;
import com._data._data.aichat.entity.ChatRoom;

import java.util.List;

public interface ChatRoomService {
    ChatRoom creatChatRoom(ChatRoomDto chatRoomDto);

    List<ChatRoom> getChatRooms();

}
