package com.bidesh.OJ.Gusion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bidesh.OJ.Gusion.entity.TestCase;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    List<TestCase> findByProblemId(Long problemId);
    List<TestCase> findByProblemIdAndIsHidden(Long problemId, Boolean isHidden);
}
