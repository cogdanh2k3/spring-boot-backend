package com.springboot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "word_search_topics")
public class WordSearchTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String topicId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank
    private String difficulty;

    @NotNull
    @Positive
    private Integer gridSize;

    @NotNull
    @Positive
    private Integer wordCount;

    private LocalDateTime createAt;

    @ElementCollection
    @CollectionTable(
            name = "topic_words",
            joinColumns = @JoinColumn(name = "topic_id")
    )
    @Column(name = "word")
    private List<String> words;


    // Constructor
    public WordSearchTopic() {
    }

    public WordSearchTopic(String topicId, String title, String difficulty, Integer gridSize, Integer wordCount, List<String> words) {
        this.topicId = topicId;
        this.title = title;
        this.difficulty = difficulty;
        this.gridSize = gridSize;
        this.wordCount = words.size();
        this.words = words;
    }

    //Getter and setter

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getTopicId() {return topicId;}
    public void setTopicId(String topicId) {this.topicId = topicId;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getDifficulty() {return difficulty;}
    public void setDifficulty(String difficulty) {this.difficulty = difficulty;}

    public Integer getGridSize() {return gridSize;}
    public void setGridSize(Integer gridSize) {this.gridSize = gridSize;}

    public Integer getWordCount() {return wordCount;}
    public void setWordCount(Integer wordCount) {this.wordCount = wordCount;}

    public LocalDateTime getCreateAt() {return createAt;}
    public void setCreateAt(LocalDateTime createAt) {this.createAt = createAt;}

    public List<String> getWords() {return words;}
    public void setWords(List<String> words) {this.words = words;}
}
