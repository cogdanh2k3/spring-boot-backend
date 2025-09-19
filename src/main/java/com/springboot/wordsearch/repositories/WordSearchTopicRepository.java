package com.springboot.wordsearch.repositories;

import com.springboot.wordsearch.entity.WordSearchTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WordSearchTopicRepository extends JpaRepository<WordSearchTopic,Long> {
    Optional<WordSearchTopic> findByTopicId(String topicId);
    Boolean existsByTopicId(String topicId);
}
