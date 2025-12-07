package com.springboot.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.admin.dto.BulkImportResult;
import com.springboot.admin.dto.QuestionCreateRequest;
import com.springboot.admin.dto.QuestionDTO;
import com.springboot.admin.dto.QuestionFilter;
import com.springboot.admin.dto.QuestionUpdateRequest;
import com.springboot.admin.service.QuestionService;
import com.springboot.auth.service.AuthService;

/**
 * Controller cho Question Management API
 * Thêm file này vào: src/main/java/com/springboot/admin/controller/QuestionController.java
 */
@RestController
@RequestMapping("/api/admin/questions")
@CrossOrigin(origins = "*")
public class QuestionController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private QuestionService questionService;

    /**
     * Check admin access
     */
    private ResponseEntity<?> checkAdminAccess(String username) {
        boolean isAdmin = authService.checkAdminRole(username);
        if (!isAdmin) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Admin access required.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
        return null;
    }

    /**
     * GET /api/admin/questions/{username}
     * Lấy danh sách câu hỏi với filter
     */
    @PostMapping("/{username}/filter")
    public ResponseEntity<?> getQuestions(
            @PathVariable String username,
            @RequestBody QuestionFilter filter) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        List<QuestionDTO> questions = questionService.getQuestions(filter);
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", questions
        ));
    }

    /**
     * GET /api/admin/questions/{username}/{questionId}
     * Lấy chi tiết một câu hỏi
     */
    @GetMapping("/{username}/{questionId}")
    public ResponseEntity<?> getQuestionById(
            @PathVariable String username,
            @PathVariable String questionId) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        QuestionDTO question = questionService.getQuestionById(questionId);
        
        if (question == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Question not found"
            ));
        }
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", question
        ));
    }

    /**
     * POST /api/admin/questions/{username}
     * Tạo câu hỏi mới
     */
    @PostMapping("/{username}")
    public ResponseEntity<?> createQuestion(
            @PathVariable String username,
            @RequestBody QuestionCreateRequest request) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        try {
            QuestionDTO newQuestion = questionService.createQuestion(request);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "data", newQuestion
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * PUT /api/admin/questions/{username}
     * Cập nhật câu hỏi
     */
    @PutMapping("/{username}")
    public ResponseEntity<?> updateQuestion(
            @PathVariable String username,
            @RequestBody QuestionUpdateRequest request) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        try {
            QuestionDTO updated = questionService.updateQuestion(request);
            
            if (updated == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "Question not found"
                ));
            }
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * DELETE /api/admin/questions/{username}/{questionId}
     * Xóa câu hỏi
     */
    @DeleteMapping("/{username}/{questionId}")
    public ResponseEntity<?> deleteQuestion(
            @PathVariable String username,
            @PathVariable String questionId) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        boolean deleted = questionService.deleteQuestion(questionId);
        
        return ResponseEntity.ok(Map.of(
                "success", deleted,
                "message", deleted ? "Question deleted successfully" : "Question not found"
        ));
    }

    /**
     * POST /api/admin/questions/{username}/bulk
     * Import nhiều câu hỏi cùng lúc
     */
    @PostMapping("/{username}/bulk")
    public ResponseEntity<?> bulkImportQuestions(
            @PathVariable String username,
            @RequestBody List<QuestionCreateRequest> questions) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        try {
            BulkImportResult result = questionService.bulkImport(questions);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
