package com._data._data.aichat.controller;

import com._data._data.aichat.dto.*;
import com._data._data.aichat.entity.ChatRoom;
import com._data._data.aichat.entity.Message;
import com._data._data.aichat.entity.Topic;
import com._data._data.aichat.entity.Translation;
import com._data._data.aichat.service.ChatRoomService;
import com._data._data.aichat.service.MessageService;
import com._data._data.aichat.service.TopicService;
import com._data._data.aichat.service.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class AiChatController {

    private final TopicService topicService;
    private final ChatRoomService chatRoomService;
    private final MessageService messageService;
    private final TranslationService translationService;

    @GetMapping("/topics")
    public List<Topic> getTopics() {
        return topicService.getAllTopics();
    }

    @PostMapping("/me/start")
    public ChatRoomInitDto startChat(@RequestBody ChatRoomDto chatRoomDto) throws Exception {
        ChatRoom chatRoom = chatRoomService.creatChatRoom(chatRoomDto);

        Message initMessage = messageService.generateBeginningMessage(chatRoom.getTopicId(), chatRoom.getId());
        translationService.getTranslation(initMessage.getId());

        ChatRoomInitDto initInfo = new ChatRoomInitDto();
        initInfo.setChatRoomId(chatRoom.getId());
        initInfo.setMessage(initMessage.getText());

        return initInfo;
    }

    @PostMapping("/me/receive")
    public MessageResponseDto receiveText(@RequestBody MessageReceiveDto messageReceiveDto) throws Exception {
        Message receivedMessage = messageService.receiveMessage(messageReceiveDto);
        return getMessageResponseDto(receivedMessage);
    }

    @PostMapping("/me/reply")
    public MessageResponseDto replyText(@RequestBody ReplyRequestDto replyRequestDto) throws Exception {
        Message generatedMessage = messageService.generateAiMessage(replyRequestDto.getChatRoomId());
        return getMessageResponseDto(generatedMessage);
    }

    @PostMapping("/me/translate")
    public TranslationResponseDto translateText(@RequestParam Long messageId) throws Exception {
        Translation translation = translationService.getTranslation(messageId);
        TranslationResponseDto translationResponseDto = new TranslationResponseDto();
        translationResponseDto.setTranslationId(translation.getId());
        translationResponseDto.setLang(translation.getLang());
        translationResponseDto.setText(translation.getTranslatedText());
        return translationResponseDto;
    }

    @GetMapping("/messages")
    public MessageResponseWrapper getAllMessagesInChatRoom(@RequestParam Long chatRoomId) {
        List<Message> messages = messageService.getAllMessages(chatRoomId);

        MessageResponseWrapper messageResponseWrapper = new MessageResponseWrapper();
        messageResponseWrapper.setMessages(messages.stream()
                .map(message -> {
                    MessageResponseDto dto = new MessageResponseDto();
                    dto.setMessageId(message.getId());
                    dto.setText(message.getText());
                    dto.setIsUser(message.getIsUser());
                    dto.setStoredAt(message.getStoredAt());
                    return dto;
                })
                .collect(Collectors.toList()));

        return messageResponseWrapper;
    }

    private MessageResponseDto getMessageResponseDto(Message message) throws Exception {
        translationService.getTranslation(message.getId());

        MessageResponseDto messageResponseDto = new MessageResponseDto();
        messageResponseDto.setMessageId(message.getId());
        messageResponseDto.setText(message.getText());
        messageResponseDto.setIsUser(message.getIsUser());
        messageResponseDto.setStoredAt(message.getStoredAt());

        return messageResponseDto;
    }
}
