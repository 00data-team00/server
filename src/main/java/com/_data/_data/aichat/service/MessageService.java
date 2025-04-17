package com._data._data.aichat.service;

import com._data._data.aichat.entity.Message;

import java.util.List;

public interface MessageService {
    List<Message> getAllMessages(Long chatRoomId);
}
