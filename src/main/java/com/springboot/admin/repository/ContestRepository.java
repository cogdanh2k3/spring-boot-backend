package com.springboot.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.admin.entity.Contest;

@Repository
public interface ContestRepository extends JpaRepository<Contest, String> {
    
    List<Contest> findByStatus(String status);
    
    List<Contest> findByCreatedBy(String createdBy);
    
    List<Contest> findByStatusOrderByStartTimeDesc(String status);
}
