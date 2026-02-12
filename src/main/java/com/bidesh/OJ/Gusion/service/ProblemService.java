package com.bidesh.OJ.Gusion.service;

import com.bidesh.OJ.Gusion.dto.testcase.TestCaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bidesh.OJ.Gusion.dto.problem.ProblemRequest;
import com.bidesh.OJ.Gusion.dto.problem.ProblemResponse;
import com.bidesh.OJ.Gusion.dto.testcase.TestCaseRequest;
import com.bidesh.OJ.Gusion.entity.TestCase;

import java.util.List;

public interface ProblemService {

    Page<ProblemResponse> findAll(Pageable pageable);

    ProblemResponse findBySlug(String slug);

    ProblemResponse create(ProblemRequest request);

    ProblemResponse update(Long id, ProblemRequest request);

    void delete(Long id);

    TestCase addTestCase(Long problemId, TestCaseRequest request);

    List<TestCaseResponse> getTestCases(Long problemId);
}
