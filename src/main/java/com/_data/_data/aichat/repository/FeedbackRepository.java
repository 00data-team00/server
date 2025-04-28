package com._data._data.aichat.repository;

import com._data._data.aichat.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findFeedbackByMessageIdAndLang(Long messageId, String lang);
}
