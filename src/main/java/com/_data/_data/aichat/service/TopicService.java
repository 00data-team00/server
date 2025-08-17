package com._data._data.aichat.service;

import com._data._data.aichat.dto.TopicListDto;
import com._data._data.aichat.dto.VisaListDto;
import com._data._data.aichat.entity.Topic;

public interface TopicService {
    Topic createIfNotExist(String title);

    Topic getByTitle(String title);

    TopicListDto getAllTopics();

    VisaListDto getVisaTopics();
}
