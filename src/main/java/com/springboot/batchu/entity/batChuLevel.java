package com.springboot.batchu.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "batchu_levels")
public class batChuLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String levelId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank
    private String difficulty;

    @NotNull
    @Positive
    private Integer questionCount;

    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(
            name = "level_questions",
            joinColumns = @JoinColumn(name = "level_id")
    )
    private List<batChuQuestion> questions;

    public batChuLevel() {
    }

    public batChuLevel(String levelId, String title, String difficulty, List<batChuQuestion> questions) {
        this.levelId = levelId;
        this.title = title;
        this.difficulty = difficulty;
        this.questionCount = questions.size();
        this.questions = questions;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLevelId() { return levelId; }
    public void setLevelId(String levelId) { this.levelId = levelId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<batChuQuestion> getQuestions() { return questions; }
    public void setQuestions(List<batChuQuestion> questions) { this.questions = questions; }
}
