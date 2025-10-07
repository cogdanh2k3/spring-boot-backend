package com.springboot.soundgame.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sound_levels")
public class SoundLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String levelId;     // ex: LevelEasy

    @NotBlank
    private String title;       // ex: "Easy Level"

    @NotBlank
    private String difficulty;  // ex: "Easy", "Normal", "Hard"

    @NotNull
    @Positive
    private Integer questionCount;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoundClip> clips;

    public SoundLevel() {}

    public SoundLevel(String levelId, String title, String difficulty, List<SoundClip> clips) {
        this.levelId = levelId;
        this.title = title;
        this.difficulty = difficulty;
        this.questionCount = (clips != null) ? clips.size() : 0;
        this.clips = clips;
        if (this.clips != null) {
            this.clips.forEach(c -> c.setLevel(this));
        }
        this.createdAt = LocalDateTime.now();
    }

    // Getters / Setters
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

    public List<SoundClip> getClips() { return clips; }
    public void setClips(List<SoundClip> clips) {
        this.clips = clips;
        if (this.clips != null) this.clips.forEach(c -> c.setLevel(this));
        this.questionCount = (clips != null) ? clips.size() : 0;
    }
}

