package com._data._data.aichat.service;

import com._data._data.aichat.entity.Message;
import com._data._data.aichat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private MessageRepository messageRepository;

    @Override
    public List<Message> getAllMessages(Long chatRoomId) {
        return messageRepository.findByChatRoomIdOrderByStoredAt(chatRoomId);
    }
}
