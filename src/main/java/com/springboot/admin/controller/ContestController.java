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

import com.springboot.admin.dto.ContestCreateRequest;
import com.springboot.admin.dto.ContestDTO;
import com.springboot.admin.dto.ContestStats;
import com.springboot.admin.dto.ContestUpdateRequest;
import com.springboot.admin.service.ContestService;
import com.springboot.auth.service.AuthService;

/**
 * Controller cho Contest Management API
 * Thêm file này vào: src/main/java/com/springboot/admin/controller/ContestController.java
 */
@RestController
@RequestMapping("/api/admin/contests")
@CrossOrigin(origins = "*")
public class ContestController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private ContestService contestService;

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
     * GET /api/admin/contests/{username}
     * Lấy danh sách cuộc thi
     */
    @GetMapping("/{username}")
    public ResponseEntity<?> getContests(@PathVariable String username) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        List<ContestDTO> contests = contestService.getAllContests();
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", contests
        ));
    }

    /**
     * GET /api/admin/contests/{username}/{contestId}
     * Lấy chi tiết một cuộc thi
     */
    @GetMapping("/{username}/{contestId}")
    public ResponseEntity<?> getContestById(
            @PathVariable String username,
            @PathVariable String contestId) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        ContestDTO contest = contestService.getContestById(contestId);
        
        if (contest == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Contest not found"
            ));
        }
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", contest
        ));
    }

    /**
     * POST /api/admin/contests/{username}
     * Tạo cuộc thi mới
     */
    @PostMapping("/{username}")
    public ResponseEntity<?> createContest(
            @PathVariable String username,
            @RequestBody ContestCreateRequest request) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        try {
            ContestDTO newContest = contestService.createContest(request);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "data", newContest
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * PUT /api/admin/contests/{username}
     * Cập nhật cuộc thi
     */
    @PutMapping("/{username}")
    public ResponseEntity<?> updateContest(
            @PathVariable String username,
            @RequestBody ContestUpdateRequest request) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        try {
            ContestDTO updated = contestService.updateContest(request);
            
            if (updated == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "Contest not found"
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
     * DELETE /api/admin/contests/{username}/{contestId}
     * Xóa cuộc thi
     */
    @DeleteMapping("/{username}/{contestId}")
    public ResponseEntity<?> deleteContest(
            @PathVariable String username,
            @PathVariable String contestId) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        boolean deleted = contestService.deleteContest(contestId);
        
        return ResponseEntity.ok(Map.of(
                "success", deleted,
                "message", deleted ? "Contest deleted successfully" : "Contest not found"
        ));
    }

    /**
     * GET /api/admin/contests/{username}/{contestId}/stats
     * Lấy thống kê cuộc thi
     */
    @GetMapping("/{username}/{contestId}/stats")
    public ResponseEntity<?> getContestStats(
            @PathVariable String username,
            @PathVariable String contestId) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        ContestStats stats = contestService.getContestStats(contestId);
        
        if (stats == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Contest not found"
            ));
        }
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", stats
        ));
    }
}
