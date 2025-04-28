package com._data._data.aichat.service;

import com._data._data.aichat.entity.Feedback;

public interface FeedbackService {
    Feedback generateFeedback(Long messageId, String messageText, String lang);

    Feedback getFeedback(Long messageId);
}
