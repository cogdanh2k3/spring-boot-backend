package com.springboot.admin.dto;

public class QuestionDTO {
    private String id;
    private String questionText;
    private java.util.List<ChoiceDTO> choices;
    private String difficulty;
    private String category;
    private Integer points;
    private Integer timeLimit;
    private Long createdAt;

    public static QuestionDTO fromEntity(com.springboot.admin.entity.Question entity) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(entity.getId());
        dto.setQuestionText(entity.getQuestionText());
        dto.setDifficulty(entity.getDifficulty());
        dto.setCategory(entity.getCategory());
        dto.setPoints(entity.getPoints());
        dto.setTimeLimit(entity.getTimeLimit());
        dto.setCreatedAt(entity.getCreatedAt());
        
        dto.setChoices(entity.getChoices().stream()
            .map(ChoiceDTO::fromEntity)
            .collect(java.util.stream.Collectors.toList()));
        
        return dto;
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

    public java.util.List<ChoiceDTO> getChoices() {
        return choices;
    }

    public void setChoices(java.util.List<ChoiceDTO> choices) {
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

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public static class ChoiceDTO {
        private String choiceLabel;
        private String choiceText;
        private Boolean isCorrect;

        public static ChoiceDTO fromEntity(com.springboot.admin.entity.QuestionChoice entity) {
            ChoiceDTO dto = new ChoiceDTO();
            dto.setChoiceLabel(entity.getChoiceLabel());
            dto.setChoiceText(entity.getChoiceText());
            dto.setIsCorrect(entity.getIsCorrect());
            return dto;
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

