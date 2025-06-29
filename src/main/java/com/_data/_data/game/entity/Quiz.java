package com._data._data.game.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long level;

    private String category;

    private String quizText;

    @ManyToMany
    @JoinTable(name = "quiz_choice",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "choice_id"))
    private List<Word> choices = new ArrayList<>();

    private String image;

    private String voice;

    private int answer;

    private String answerScript;
}
