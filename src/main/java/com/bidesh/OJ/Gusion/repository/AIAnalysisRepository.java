package com.bidesh.OJ.Gusion.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bidesh.OJ.Gusion.entity.AIAnalysis;

@Repository
public interface AIAnalysisRepository extends JpaRepository<AIAnalysis, Long> {
    Optional<AIAnalysis> findBySubmissionId(UUID submissionId);
}
