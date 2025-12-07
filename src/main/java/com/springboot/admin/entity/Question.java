package com.springboot.admin.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    private String id;
    
    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<QuestionChoice> choices = new ArrayList<>();
    
    @Column(nullable = false)
    private String difficulty;
    
    @Column(nullable = false)
    private String category;
    
    @Column(nullable = false)
    private Integer points;
    
    @Column(name = "time_limit", nullable = false)
    private Integer timeLimit;
    
    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    // Constructors
    public Question() {
    }

    public Question(String id, String questionText, String difficulty, String category, 
                    Integer points, Integer timeLimit, Long createdAt) {
        this.id = id;
        this.questionText = questionText;
        this.difficulty = difficulty;
        this.category = category;
        this.points = points;
        this.timeLimit = timeLimit;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<QuestionChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<QuestionChoice> choices) {
        this.choices = choices;
        // Set bidirectional relationship
        for (QuestionChoice choice : choices) {
            choice.setQuestion(this);
        }
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
