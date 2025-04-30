package com._data._data.aichat.service;

import com._data._data.aichat.dto.TopicDto;
import com._data._data.aichat.dto.TopicListDto;
import com._data._data.aichat.entity.Topic;
import com._data._data.aichat.exception.TopicNotFoundException;
import com._data._data.aichat.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    private static TopicDto getTopicDto(Topic topic) {
        TopicDto topicDto = new TopicDto();
        topicDto.setCategory(topic.getCategory());
        topicDto.setTitle(topic.getTitle());
        topicDto.setDescription(topic.getDescription());
        topicDto.setUserRole(topic.getUserRole());
        topicDto.setAiRole(topic.getAiRole());
        return topicDto;
    }

    @Override
    public Topic createIfNotExist(String title) {
        return null;
    }

    @Override
    public Topic getByTitle(String title) {
        return topicRepository.findByTitle(title).orElseThrow(() -> new TopicNotFoundException(title));
    }

    @Override
    public TopicListDto getAllTopics() {

        String[] categories = {"필수/일상", "문화/여가", "취업/업무"};

        TopicListDto topicListDto = new TopicListDto();

        for (int i = 0; i < categories.length; i++) {

            List<Topic> topics = topicRepository.findByCategory(categories[i]);

            if (i == 0) {
                topicListDto.setEssentialTopics(topics.stream()
                        .map(TopicServiceImpl::getTopicDto)
                        .collect(Collectors.toList()));
            }
            else if (i == 1) {
                topicListDto.setCulturalTopics(topics.stream()
                        .map(TopicServiceImpl::getTopicDto)
                        .collect(Collectors.toList()));
            }
            else {
                topicListDto.setBusinessTopics(topics.stream()
                        .map(TopicServiceImpl::getTopicDto)
                        .collect(Collectors.toList()));
            }
        }

        return topicListDto;
    }

}
