package com._data._data.aichat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "feedback_lang", nullable = false)
    private String lang;

    private String feedbackText;

    private LocalDateTime feedbackAt;

    @PrePersist
    public void prePersist() {
        feedbackAt = LocalDateTime.now();
    }
}
