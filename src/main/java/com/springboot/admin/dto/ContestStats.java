package com.springboot.admin.dto;

public class ContestStats {
    private String contestId;
    private String title;
    private Integer totalParticipants;
    private Integer completedParticipants;
    private Double averageScore;
    private Integer highestScore;
    private Integer lowestScore;
    private String status;

    public ContestStats() {
    }

    public ContestStats(String contestId, String title, Integer totalParticipants, 
                       Integer completedParticipants, Double averageScore, 
                       Integer highestScore, Integer lowestScore, String status) {
        this.contestId = contestId;
        this.title = title;
        this.totalParticipants = totalParticipants;
        this.completedParticipants = completedParticipants;
        this.averageScore = averageScore;
        this.highestScore = highestScore;
        this.lowestScore = lowestScore;
        this.status = status;
    }

    // Getters and Setters
    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTotalParticipants() {
        return totalParticipants;
    }

    public void setTotalParticipants(Integer totalParticipants) {
        this.totalParticipants = totalParticipants;
    }

    public Integer getCompletedParticipants() {
        return completedParticipants;
    }

    public void setCompletedParticipants(Integer completedParticipants) {
        this.completedParticipants = completedParticipants;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Integer getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(Integer highestScore) {
        this.highestScore = highestScore;
    }

    public Integer getLowestScore() {
        return lowestScore;
    }

    public void setLowestScore(Integer lowestScore) {
        this.lowestScore = lowestScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
