package com.springboot.batchu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public class batChuQuestion {
    @NotBlank
    private String questionText;

    @NotBlank
    private String answer;

    @NotBlank
    private String imageUrl;

    @Column
    private String suggestion;

    // Constructors
    public batChuQuestion() {
    }

    public batChuQuestion(String questionText, String answer, String imageUrl, String suggestion) {
        this.questionText = questionText;
        this.answer = answer;
        this.imageUrl = imageUrl;
        this.suggestion = suggestion;
    }

    // Getters and Setters
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
}