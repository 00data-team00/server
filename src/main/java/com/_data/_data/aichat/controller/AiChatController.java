package com._data._data.aichat.controller;

import com._data._data.aichat.dto.*;
import com._data._data.aichat.entity.ChatRoom;
import com._data._data.aichat.entity.Message;
import com._data._data.aichat.entity.Topic;
import com._data._data.aichat.service.ChatRoomService;
import com._data._data.aichat.service.MessageService;
import com._data._data.aichat.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class AiChatController {

    private final TopicService topicService;
    private final ChatRoomService chatRoomService;
    private final MessageService messageService;

    @GetMapping("/topics")
    public List<Topic> getTopics() {
        return topicService.getAllTopics();
    }

    @PostMapping("/start")
    public ChatRoomInitDto startChat(@RequestBody ChatRoomDto chatRoomDto) {
        ChatRoom chatRoom = chatRoomService.creatChatRoom(chatRoomDto);

        Message initMessage = messageService.generateBeginningMessage(chatRoom.getTopicId(), chatRoom.getId());

        ChatRoomInitDto initInfo = new ChatRoomInitDto();
        initInfo.setChatRoomId(chatRoom.getId());
        initInfo.setMessage(initMessage.getText());

        return initInfo;
    }

    @PostMapping("/receive")
    public MessageResponseDto receiveText(@RequestBody MessageReceiveDto messageReceiveDto) {
        Message receivedMessage = messageService.receiveMessage(messageReceiveDto);

        MessageResponseDto messageResponseDto = new MessageResponseDto();
        messageResponseDto.setMessageId(receivedMessage.getId());
        messageResponseDto.setText(receivedMessage.getText());
        messageResponseDto.setIsUser(receivedMessage.getIsUser());
        messageResponseDto.setStoredAt(receivedMessage.getStoredAt());

        return messageResponseDto;
    }

    @PostMapping("/reply")
    public MessageResponseDto replyText(@RequestBody ReplyRequestDto replyRequestDto) {
        Message generatedMessage = messageService.generateAiMessage(replyRequestDto.getChatRoomId());

        MessageResponseDto messageResponseDto = new MessageResponseDto();
        messageResponseDto.setMessageId(generatedMessage.getId());
        messageResponseDto.setText(generatedMessage.getText());
        messageResponseDto.setIsUser(generatedMessage.getIsUser());
        messageResponseDto.setStoredAt(generatedMessage.getStoredAt());

        return messageResponseDto;
    }
}
