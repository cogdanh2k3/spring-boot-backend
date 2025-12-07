package com.springboot.admin.dto;

import java.util.List;

public class QuestionUpdateRequest {
    private String id;
    private String questionText;
    private List<QuestionCreateRequest.ChoiceDTO> choices;
    private String difficulty;
    private String category;
    private Integer points;
    private Integer timeLimit;

    // Constructors
    public QuestionUpdateRequest() {
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

    public List<QuestionCreateRequest.ChoiceDTO> getChoices() {
        return choices;
    }

    public void setChoices(List<QuestionCreateRequest.ChoiceDTO> choices) {
        this.choices = choices;
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
}
