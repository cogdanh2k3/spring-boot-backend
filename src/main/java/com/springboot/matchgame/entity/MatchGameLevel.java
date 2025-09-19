package com.springboot.matchgame.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "matchgame_levels")
public class MatchGameLevel {
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
    private Integer pairCount;

    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(
            name = "level_pairs",
            joinColumns = @JoinColumn(name = "level_id")
    )
    private List<MatchPair> pairs;

    // Constructors
    public MatchGameLevel() {
    }

    public MatchGameLevel(String levelId, String title, String difficulty, List<MatchPair> pairs) {
        this.levelId = levelId;
        this.title = title;
        this.difficulty = difficulty;
        this.pairCount = pairs.size();
        this.pairs = pairs;
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

    public Integer getPairCount() { return pairCount; }
    public void setPairCount(Integer pairCount) { this.pairCount = pairCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<MatchPair> getPairs() { return pairs; }
    public void setPairs(List<MatchPair> pairs) { this.pairs = pairs; }
}