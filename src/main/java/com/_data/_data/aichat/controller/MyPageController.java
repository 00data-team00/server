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
public class MyPageController {

    private final ChatRoomService chatRoomService;
    private final FeedbackService feedbackService;
    private final MessageService messageService;

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
