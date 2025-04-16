package com._data._data.aichat.service;

import com._data._data.aichat.entity.Topic;
import com._data._data.aichat.exception.TopicNotFoundException;
import com._data._data.aichat.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    @Override
    public Topic createIfNotExist(String title) {
        return null;
    }

    @Override
    public Topic getByTitle(String title) {
        return topicRepository.findByTitle(title).orElseThrow(() -> new TopicNotFoundException(title));
    }

    @Override
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }
}
