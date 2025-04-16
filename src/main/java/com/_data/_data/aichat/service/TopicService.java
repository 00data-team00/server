package com._data._data.aichat.service;

import com._data._data.aichat.entity.Topic;

import java.util.List;

public interface TopicService {
    Topic createIfNotExist(String title);

    Topic getByTitle(String title);

    List<Topic> getAllTopics();
}
