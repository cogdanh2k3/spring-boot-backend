package com.springboot.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.admin.entity.ContestQuestion;

@Repository
public interface ContestQuestionRepository extends JpaRepository<ContestQuestion, Long> {
}
