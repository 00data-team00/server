package com._data._data.aichat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "translations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "translation_lang", nullable = false)
    private String lang;

    private String translatedText;

    private LocalDateTime translatedAt;

    @PrePersist
    public void prePersist() {
        translatedAt = LocalDateTime.now();
    }
}
