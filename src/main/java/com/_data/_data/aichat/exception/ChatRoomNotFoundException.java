package com._data._data.aichat.exception;

public class ChatRoomNotFoundException extends NotFoundException{
    public ChatRoomNotFoundException(Long ChatRoomId) {
        super("ChatRoom not found with id: " + ChatRoomId);
    }
}
