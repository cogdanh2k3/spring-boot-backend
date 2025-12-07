package com.springboot.admin.dto;

import java.util.List;

public class QuestionCreateRequest {
    private String questionText;
    private List<ChoiceDTO> choices;
    private String difficulty;
    private String category;
    private Integer points;
    private Integer timeLimit;

    // Constructors
    public QuestionCreateRequest() {
    }

    // Getters and Setters
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<ChoiceDTO> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoiceDTO> choices) {
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

    public static class ChoiceDTO {
        private String choiceLabel;
        private String choiceText;
        private Boolean isCorrect;

        // Constructors
        public ChoiceDTO() {
        }

        public ChoiceDTO(String choiceLabel, String choiceText, Boolean isCorrect) {
            this.choiceLabel = choiceLabel;
            this.choiceText = choiceText;
            this.isCorrect = isCorrect;
        }

        // Getters and Setters
        public String getChoiceLabel() {
            return choiceLabel;
        }

        public void setChoiceLabel(String choiceLabel) {
            this.choiceLabel = choiceLabel;
        }

        public String getChoiceText() {
            return choiceText;
        }

        public void setChoiceText(String choiceText) {
            this.choiceText = choiceText;
        }

        public Boolean getIsCorrect() {
            return isCorrect;
        }

        public void setIsCorrect(Boolean isCorrect) {
            this.isCorrect = isCorrect;
        }
    }
}
