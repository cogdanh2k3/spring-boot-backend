package com.springboot.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springboot.admin.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
    
    List<Question> findByCategory(String category);
    
    List<Question> findByDifficulty(String difficulty);
    
    @Query("SELECT q FROM Question q WHERE " +
           "(:category IS NULL OR :category = '' OR q.category = :category) AND " +
           "(:difficulty IS NULL OR :difficulty = '' OR q.difficulty = :difficulty) AND " +
           "(:searchQuery IS NULL OR :searchQuery = '' OR LOWER(q.questionText) LIKE LOWER(CONCAT('%', :searchQuery, '%')))")
    List<Question> findWithFilters(
        @Param("category") String category,
        @Param("difficulty") String difficulty,
        @Param("searchQuery") String searchQuery
    );
}
