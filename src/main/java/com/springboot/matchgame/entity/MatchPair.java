package com.springboot.matchgame.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public class MatchPair {
    @NotBlank
    private String definition;

    @NotBlank
    private String word;

    // Constructors
    public MatchPair() {
    }

    public MatchPair(String definition, String word) {
        this.definition = definition;
        this.word = word;
    }

    // Getters and Setters
    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
}