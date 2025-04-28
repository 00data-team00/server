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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "AiChat", description = "AI 회화 관련 API")
public class AiChatController {

    private final TopicService topicService;
    private final ChatRoomService chatRoomService;
    private final MessageService messageService;
    private final TranslationService translationService;

    @Operation(
            summary = "대화 주제 목록",
            description = "AI 회화를 위한 주제 목록을 반환합니다."
    )
    @GetMapping("/topics")
    public List<Topic> getTopics() {
        return topicService.getAllTopics();
    }

    @Operation(
            summary = "채팅 시작",
            description = "유저가 선택한 주제로 채팅방을 생성하고 채팅방 ID와 함께 대화 시작 메세지를 반환합니다."
    )
    @PostMapping("/me/start")
    public ChatRoomInitDto startChat(@RequestParam Long topicId) throws Exception {
        ChatRoom chatRoom = chatRoomService.creatChatRoom(topicId);

        Message initMessage = messageService.generateBeginningMessage(chatRoom.getTopicId(), chatRoom.getId());
        translationService.getTranslation(initMessage.getId());

        ChatRoomInitDto initInfo = new ChatRoomInitDto();
        initInfo.setChatRoomId(chatRoom.getId());
        initInfo.setMessage(initMessage.getText());

        return initInfo;
    }

    @Operation(
            summary = "유저 메세지 저장 및 AI 응답",
            description = "stt로 생성된 유저 메세지를 저장하고 저장된 유저 메세지의 정보와 함께 AI 응답 메세지를 반환합니다."
    )
    @PostMapping("/me/receive")
    public MessageReceiveAndResponseWrapper receiveText(@RequestBody MessageReceiveDto messageReceiveDto) throws Exception {
        Message receivedMessage = messageService.receiveMessage(messageReceiveDto);
        Message generatedMessage = messageService.generateAiMessage(messageReceiveDto.getChatRoomId());

        MessageReceiveAndResponseWrapper messageReceiveAndResponseWrapper = new MessageReceiveAndResponseWrapper();
        messageReceiveAndResponseWrapper.setUserMessage(getMessageResponseDto(receivedMessage));
        messageReceiveAndResponseWrapper.setAiMessage(getMessageResponseDto(generatedMessage));

        return messageReceiveAndResponseWrapper;
    }

    @Operation(
            summary = "메세지 번역",
            description = "해당 메세지를 번역한 결과를 반환합니다."
    )
    @PostMapping("/me/translate")
    public TranslationResponseDto translateText(@RequestParam Long messageId) throws Exception {
        Translation translation = translationService.getTranslation(messageId);
        TranslationResponseDto translationResponseDto = new TranslationResponseDto();
        translationResponseDto.setTranslationId(translation.getId());
        translationResponseDto.setLang(translation.getLang());
        translationResponseDto.setText(translation.getTranslatedText());
        return translationResponseDto;
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
