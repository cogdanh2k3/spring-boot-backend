package com.springboot.batchu.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "batchu_user_progress")
public class batChuUserProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank
    private String levelId;

    @NotNull
    private Boolean isCompleted = false;

    private String timeSpent;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    // Constructors
    public batChuUserProgress() {}

    public batChuUserProgress(String username, String levelId, Boolean isCompleted) {
        this.username = username;
        this.levelId = levelId;
        this.isCompleted = isCompleted;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getLevelId() { return levelId; }
    public void setLevelId(String levelId) { this.levelId = levelId; }

    public Boolean getCompleted() { return isCompleted; }
    public void setCompleted(Boolean completed) { isCompleted = completed; }

    public String getTimeSpent() { return timeSpent; }
    public void setTimeSpent(String timeSpent) { this.timeSpent = timeSpent; }

    public LocalDateTime getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDateTime completionDate) { this.completionDate = completionDate; }
}