package com.springboot.quizgame.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
@Table(name = "quiz_questions")
public class QuizQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String questionText;

    @NotBlank
    private String answer;

    @NotBlank
    private String category;

    @ElementCollection
    @Column(name = "choice")
    private List<String> choices;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private QuizGameLevel level;

    // Constructors
    public QuizQuestion() {
    }

    public QuizQuestion(String questionText, String answer, String category, List<String> choices) {
        this.questionText = questionText;
        this.answer = answer;
        this.category = category;
        this.choices = choices;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getChoices() { return choices; }
    public void setChoices(List<String> choices) { this.choices = choices; }

    public QuizGameLevel getLevel() { return level; }
    public void setLevel(QuizGameLevel level) { this.level = level; }
}