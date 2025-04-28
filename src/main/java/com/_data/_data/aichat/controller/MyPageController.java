package com._data._data.aichat.controller;

import com._data._data.aichat.dto.ChatRoomResponseDto;
import com._data._data.aichat.dto.ChatRoomResponseWrapper;
import com._data._data.aichat.dto.MessageResponseWithFeedback;
import com._data._data.aichat.dto.MessageResponseWrapper;
import com._data._data.aichat.entity.ChatRoom;
import com._data._data.aichat.entity.Feedback;
import com._data._data.aichat.entity.Message;
import com._data._data.aichat.service.ChatRoomService;
import com._data._data.aichat.service.FeedbackService;
import com._data._data.aichat.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@Tag(name = "ChatHistory", description = "유저의 회화 기록 관련 API")
public class MyPageController {

    private final ChatRoomService chatRoomService;
    private final FeedbackService feedbackService;
    private final MessageService messageService;

    @Operation(
            summary = "유저의 채팅방 목록",
            description = "유저가 생성한 채팅방 목록을 최근 생성 순으로 반환합니다"
    )
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

    @Operation(
            summary = "채팅방 내 모든 메세지",
            description = "해당 채팅방의 모든 메세지를 반환합니다. 유저 메세지의 경우에는 피드백과 함께 반환합니다."
    )
    @GetMapping("/chat/messages")
    public MessageResponseWrapper getAllMessagesInChatRoom(@RequestParam Long chatRoomId) {
        List<Message> messages = messageService.getAllMessages(chatRoomId);

        MessageResponseWrapper messageResponseWrapper = new MessageResponseWrapper();
        messageResponseWrapper.setMessages(messages.stream()
                .map(message -> {
                    MessageResponseWithFeedback dto = new MessageResponseWithFeedback();

                    if (message.getIsUser()) {
                        Feedback feedback = feedbackService.getFeedback(message.getId());
                        dto.setFeedbackLang(feedback.getLang());
                        dto.setFeedbackContent(feedback.getFeedbackText());
                    }

                    dto.setMessageId(message.getId());
                    dto.setText(message.getText());
                    dto.setIsUser(message.getIsUser());
                    dto.setStoredAt(message.getStoredAt());

                    return dto;
                })
                .collect(Collectors.toList()));

        return messageResponseWrapper;
    }
}
