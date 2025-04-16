package com._data._data.aichat.exception;

public class MessageNotFoundException extends NotFoundException {
    public MessageNotFoundException(Long id) {
        super("Message not found with id " + id);
    }
}
