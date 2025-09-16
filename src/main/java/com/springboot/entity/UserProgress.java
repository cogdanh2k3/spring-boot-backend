package com.springboot.entity;

import com.springboot.SoftwareEngineer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users_progress")
public class UserProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank
    private String topicId;

    @NotNull
    private Boolean isCompleted = false;

    private String timeSpent;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    //Constructors
    public UserProgress() {}

    public UserProgress(String username, String topicId, Boolean isCompleted) {
        this.username = username;
        this.topicId = topicId;
        this.isCompleted = isCompleted;
    }

    //Getters and Setters
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getTopicId() {return topicId;}
    public void setTopicId(String topicId) {this.topicId = topicId;}

    public Boolean getCompleted() {return isCompleted;}
    public void setCompleted(Boolean completed) {isCompleted = completed;}

    public String getTimeSpent() {return timeSpent;}
    public void setTimeSpent(String timeSpent) {this.timeSpent = timeSpent;}

    public LocalDateTime getCompletionDate() {return completionDate;}
    public void setCompletionDate(LocalDateTime completionDate) {this.completionDate = completionDate;}
}
