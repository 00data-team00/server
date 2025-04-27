package com._data._data.aichat.controller;

import com._data._data.aichat.dto.ChatRoomResponseDto;
import com._data._data.aichat.dto.ChatRoomResponseWrapper;
import com._data._data.aichat.entity.ChatRoom;
import com._data._data.aichat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MyPageController {

    private final ChatRoomService chatRoomService;
    @GetMapping("/chat/chatrooms")
    public ChatRoomResponseWrapper getMyChatRooms() {
        List<ChatRoom> chatRooms = chatRoomService.getChatRooms();

        ChatRoomResponseWrapper chatRoomResponseWrapper = new ChatRoomResponseWrapper();
        chatRoomResponseWrapper.setChatRooms(chatRooms.stream()
                .map(chatroom -> {
                    ChatRoomResponseDto dto = new ChatRoomResponseDto();
                    dto.setChatRoomId(chatroom.getId());
                    dto.setTitle(chatroom.getTitle());
                    dto.setDescription(chatroom.getDescription());
                    dto.setCreatedAt(chatroom.getCreatedAt());
                    dto.setIsFinished(chatroom.getIsFinished());
                    return dto;
                })
                .collect(Collectors.toList()));

        return chatRoomResponseWrapper;
    }
}
