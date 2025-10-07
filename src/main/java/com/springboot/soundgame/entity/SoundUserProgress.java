package com.springboot.soundgame.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "sound_user_progress")
public class SoundUserProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String levelId;

    private boolean completed = false;
    private LocalDateTime completionDate;
    private String timeSpent; // e.g. "00:02:15"

    public SoundUserProgress() {}

    public SoundUserProgress(String username, String levelId, boolean completed, String timeSpent) {
        this.username = username;
        this.levelId = levelId;
        this.completed = completed;
        this.timeSpent = timeSpent;
        if (completed) this.completionDate = LocalDateTime.now();
    }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getLevelId() { return levelId; }
    public void setLevelId(String levelId) { this.levelId = levelId; }

    public boolean getCompleted() { return completed; }
    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed && this.completionDate == null) this.completionDate = LocalDateTime.now();
    }

    public LocalDateTime getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDateTime completionDate) { this.completionDate = completionDate; }

    public String getTimeSpent() { return timeSpent; }
    public void setTimeSpent(String timeSpent) { this.timeSpent = timeSpent; }
}


