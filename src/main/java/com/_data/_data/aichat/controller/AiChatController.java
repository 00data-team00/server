package com._data._data.aichat.controller;

import com._data._data.aichat.dto.ChatRoomDto;
import com._data._data.aichat.dto.ChatRoomInitDto;
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

}
