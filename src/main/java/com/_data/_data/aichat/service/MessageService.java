package com._data._data.aichat.service;

import com._data._data.aichat.dto.MessageReceiveDto;
import com._data._data.aichat.entity.Message;

import java.util.List;

public interface MessageService {

    Message receiveMessage(MessageReceiveDto messageReceiveDto);

    Message generateBeginningMessage(Long topicId, Long chatRoomId);

    Message generateAiMessage(Long chatRoomId);

    List<Message> getAllMessages(Long chatRoomId);
}
