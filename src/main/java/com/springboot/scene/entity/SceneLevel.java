package com.springboot.scene.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "scene_levels")
public class SceneLevel {
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "level_id")
    private List<SceneLocation> locations;

    // Constructors
    public SceneLevel() {}

    public SceneLevel(String levelId, String title, String difficulty, List<SceneLocation> locations) {
        this.levelId = levelId;
        this.title = title;
        this.difficulty = difficulty;
        this.questionCount = locations.size();
        this.locations = locations;
        this.createdAt = LocalDateTime.now();
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

    public List<SceneLocation> getLocations() { return locations; }
    public void setLocations(List<SceneLocation> locations) { this.locations = locations; }
}