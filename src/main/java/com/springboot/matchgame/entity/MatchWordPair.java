package com.springboot.matchgame.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public class MatchWordPair {
    @NotBlank
    private String word;

    @NotBlank
    @Column(name = "definition_text")
    private String definition;

    // Constructors
    public MatchWordPair() {
    }

    public MatchWordPair(String word, String definition) {
        this.word = word;
        this.definition = definition;
    }

    // Getters and Setters
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }
}