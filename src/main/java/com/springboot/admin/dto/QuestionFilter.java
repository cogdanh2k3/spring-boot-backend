package com.springboot.admin.dto;

public class QuestionFilter {
    private String category;
    private String difficulty;
    private String searchQuery;

    // Constructors
    public QuestionFilter() {
    }

    public QuestionFilter(String category, String difficulty, String searchQuery) {
        this.category = category;
        this.difficulty = difficulty;
        this.searchQuery = searchQuery;
    }

    // Getters and Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
}
