package com.springboot.admin.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.admin.dto.ContestCreateRequest;
import com.springboot.admin.dto.ContestDTO;
import com.springboot.admin.dto.ContestStats;
import com.springboot.admin.dto.ContestUpdateRequest;
import com.springboot.admin.entity.ContestQuestion;
import com.springboot.admin.repository.ContestRepository;

@Service
public class ContestService {

    @Autowired
    private ContestRepository contestRepository;

    /**
     * Lấy tất cả contests
     */
    public List<ContestDTO> getAllContests() {
        return contestRepository.findAll().stream()
            .map(ContestDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết một contest
     */
    public ContestDTO getContestById(String contestId) {
        return contestRepository.findById(contestId)
            .map(ContestDTO::fromEntity)
            .orElse(null);
    }

    /**
     * Tạo contest mới
     */
    @Transactional
    public ContestDTO createContest(ContestCreateRequest request) {
        String contestId = UUID.randomUUID().toString();
        Long currentTime = System.currentTimeMillis();

        // Determine status based on start time
        String status = determineStatus(request.getStartTime(), request.getEndTime());

        com.springboot.admin.entity.Contest entity = new com.springboot.admin.entity.Contest();
        entity.setId(contestId);
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setStartTime(request.getStartTime());
        entity.setEndTime(request.getEndTime());
        entity.setDuration(request.getDuration());
        entity.setTotalQuestions(request.getQuestionIds() != null ? request.getQuestionIds().size() : 0);
        entity.setStatus(status);
        entity.setParticipantCount(0);
        entity.setMaxParticipants(request.getMaxParticipants() != null ? request.getMaxParticipants() : 100);
        entity.setCreatedBy(request.getCreatedBy());
        entity.setCreatedAt(currentTime);

        // Add contest questions
        if (request.getQuestionIds() != null && !request.getQuestionIds().isEmpty()) {
            List<ContestQuestion> contestQuestions = IntStream.range(0, request.getQuestionIds().size())
                .mapToObj(i -> {
                    ContestQuestion cq = new ContestQuestion();
                    cq.setQuestionId(request.getQuestionIds().get(i));
                    cq.setQuestionOrder(i + 1);
                    cq.setContest(entity);
                    return cq;
                })
                .collect(Collectors.toList());
            entity.setContestQuestions(contestQuestions);
        }

        com.springboot.admin.entity.Contest saved = contestRepository.save(entity);
        return ContestDTO.fromEntity(saved);
    }

    /**
     * Cập nhật contest
     */
    @Transactional
    public ContestDTO updateContest(ContestUpdateRequest request) {
        com.springboot.admin.entity.Contest entity = contestRepository.findById(request.getId())
            .orElseThrow(() -> new RuntimeException("Contest not found"));

        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setStartTime(request.getStartTime());
        entity.setEndTime(request.getEndTime());
        entity.setDuration(request.getDuration());
        entity.setMaxParticipants(request.getMaxParticipants());
        
        // Update status if provided
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        } else {
            entity.setStatus(determineStatus(request.getStartTime(), request.getEndTime()));
        }

        // Update questions
        if (request.getQuestionIds() != null) {
            entity.getContestQuestions().clear();
            
            List<ContestQuestion> newQuestions = IntStream.range(0, request.getQuestionIds().size())
                .mapToObj(i -> {
                    ContestQuestion cq = new ContestQuestion();
                    cq.setQuestionId(request.getQuestionIds().get(i));
                    cq.setQuestionOrder(i + 1);
                    cq.setContest(entity);
                    return cq;
                })
                .collect(Collectors.toList());
            entity.setContestQuestions(newQuestions);
            entity.setTotalQuestions(newQuestions.size());
        }

        com.springboot.admin.entity.Contest updated = contestRepository.save(entity);
        return ContestDTO.fromEntity(updated);
    }

    /**
     * Xóa contest
     */
    @Transactional
    public boolean deleteContest(String contestId) {
        if (contestRepository.existsById(contestId)) {
            contestRepository.deleteById(contestId);
            return true;
        }
        return false;
    }

    /**
     * Determine contest status based on time
     */
    private String determineStatus(Long startTime, Long endTime) {
        Long currentTime = System.currentTimeMillis();
        
        if (currentTime < startTime) {
            return "scheduled";
        } else if (currentTime >= startTime && currentTime <= endTime) {
            return "live";
        } else {
            return "ended";
        }
    }

    /**
     * Lấy contests theo status
     */
    public List<ContestDTO> getContestsByStatus(String status) {
        return contestRepository.findByStatus(status).stream()
            .map(ContestDTO::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Lấy thống kê contest
     */
    public ContestStats getContestStats(String contestId) {
        com.springboot.admin.entity.Contest entity = contestRepository.findById(contestId)
            .orElse(null);
        
        if (entity == null) {
            return null;
        }

        // For now, return basic stats. You can expand this with actual participant data
        ContestStats stats = new ContestStats();
        stats.setContestId(entity.getId());
        stats.setTitle(entity.getTitle());
        stats.setTotalParticipants(entity.getParticipantCount());
        stats.setCompletedParticipants(0); // Would need actual participant tracking
        stats.setAverageScore(0.0);
        stats.setHighestScore(0);
        stats.setLowestScore(0);
        stats.setStatus(entity.getStatus());
        
        return stats;
    }
}

