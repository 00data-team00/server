package com._data._data.aichat.exception;

public class TopicNotFoundException extends NotFoundException {
    public TopicNotFoundException(Long id) {
        super("Topic not found with id: " + id);
    }

    public TopicNotFoundException(String title) {
        super("Topic not found with title: " + title);
    }
}
