package com.springboot.admin.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.admin.dto.BulkImportResult;
import com.springboot.admin.dto.QuestionCreateRequest;
import com.springboot.admin.dto.QuestionDTO;
import com.springboot.admin.dto.QuestionFilter;
import com.springboot.admin.dto.QuestionUpdateRequest;
import com.springboot.admin.entity.QuestionChoice;
import com.springboot.admin.repository.QuestionRepository;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * Lấy danh sách câu hỏi với filter
     */
    public List<QuestionDTO> getQuestions(QuestionFilter filter) {
        String category = filter.getCategory() != null ? filter.getCategory() : "";
        String difficulty = filter.getDifficulty() != null ? filter.getDifficulty() : "";
        String searchQuery = filter.getSearchQuery() != null ? filter.getSearchQuery() : "";

        List<com.springboot.admin.entity.Question> entities = questionRepository.findWithFilters(
            category, difficulty, searchQuery
        );

        return entities.stream()
            .map(QuestionDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết một câu hỏi
     */
    public QuestionDTO getQuestionById(String questionId) {
        return questionRepository.findById(questionId)
            .map(QuestionDTO::fromEntity)
            .orElse(null);
    }

    /**
     * Tạo câu hỏi mới
     */
    @Transactional
    public QuestionDTO createQuestion(QuestionCreateRequest request) {
        String questionId = UUID.randomUUID().toString();
        Long currentTime = System.currentTimeMillis();

        com.springboot.admin.entity.Question entity = new com.springboot.admin.entity.Question();
        entity.setId(questionId);
        entity.setQuestionText(request.getQuestionText());
        entity.setDifficulty(request.getDifficulty());
        entity.setCategory(request.getCategory());
        entity.setPoints(request.getPoints() != null ? request.getPoints() : 10);
        entity.setTimeLimit(request.getTimeLimit() != null ? request.getTimeLimit() : 30);
        entity.setCreatedAt(currentTime);

        // Add choices
        if (request.getChoices() != null && !request.getChoices().isEmpty()) {
            List<QuestionChoice> choices = request.getChoices().stream()
                .map(choice -> {
                    QuestionChoice qc = new QuestionChoice();
                    qc.setChoiceLabel(choice.getChoiceLabel());
                    qc.setChoiceText(choice.getChoiceText());
                    qc.setIsCorrect(choice.getIsCorrect());
                    qc.setQuestion(entity);
                    return qc;
                })
                .collect(Collectors.toList());
            entity.setChoices(choices);
        }

        com.springboot.admin.entity.Question saved = questionRepository.save(entity);
        return QuestionDTO.fromEntity(saved);
    }

    /**
     * Cập nhật câu hỏi
     */
    @Transactional
    public QuestionDTO updateQuestion(QuestionUpdateRequest request) {
        com.springboot.admin.entity.Question entity = questionRepository.findById(request.getId())
            .orElseThrow(() -> new RuntimeException("Question not found"));

        entity.setQuestionText(request.getQuestionText());
        entity.setDifficulty(request.getDifficulty());
        entity.setCategory(request.getCategory());
        entity.setPoints(request.getPoints());
        entity.setTimeLimit(request.getTimeLimit());

        // Update choices
        if (request.getChoices() != null) {
            entity.getChoices().clear();
            
            List<QuestionChoice> newChoices = request.getChoices().stream()
                .map(choice -> {
                    QuestionChoice qc = new QuestionChoice();
                    qc.setChoiceLabel(choice.getChoiceLabel());
                    qc.setChoiceText(choice.getChoiceText());
                    qc.setIsCorrect(choice.getIsCorrect());
                    qc.setQuestion(entity);
                    return qc;
                })
                .collect(Collectors.toList());
            entity.setChoices(newChoices);
        }

        com.springboot.admin.entity.Question updated = questionRepository.save(entity);
        return QuestionDTO.fromEntity(updated);
    }

    /**
     * Xóa câu hỏi
     */
    @Transactional
    public boolean deleteQuestion(String questionId) {
        if (questionRepository.existsById(questionId)) {
            questionRepository.deleteById(questionId);
            return true;
        }
        return false;
    }

    /**
     * Lấy tất cả câu hỏi (không filter)
     */
    public List<QuestionDTO> getAllQuestions() {
        return questionRepository.findAll().stream()
            .map(QuestionDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Bulk import questions
     */
    @Transactional
    public BulkImportResult bulkImport(List<QuestionCreateRequest> requests) {
        int total = requests.size();
        int success = 0;
        int failed = 0;

        for (QuestionCreateRequest request : requests) {
            try {
                createQuestion(request);
                success++;
            } catch (Exception e) {
                failed++;
            }
        }

        String message = String.format("Imported %d/%d questions successfully", success, total);
        return new BulkImportResult(total, success, failed, message);
    }
}

