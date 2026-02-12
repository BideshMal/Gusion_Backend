package com.bidesh.OJ.Gusion.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.bidesh.OJ.Gusion.dto.testcase.TestCaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ✅ THESE IMPORTS MATCH YOUR FILE STRUCTURE EXACTLY
import com.bidesh.OJ.Gusion.dto.problem.ProblemRequest;
import com.bidesh.OJ.Gusion.dto.problem.ProblemResponse;
import com.bidesh.OJ.Gusion.dto.testcase.TestCaseRequest;

import com.bidesh.OJ.Gusion.entity.Problem;
import com.bidesh.OJ.Gusion.entity.TestCase;
import com.bidesh.OJ.Gusion.repository.ProblemRepository;
import com.bidesh.OJ.Gusion.repository.TestCaseRepository;
import com.bidesh.OJ.Gusion.service.ProblemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProblemResponse> findAll(Pageable pageable) {
        return problemRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemResponse findBySlug(String slug) {
        Problem problem = problemRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found: " + slug));
        return toResponse(problem);
    }

    @Override
    @Transactional
    public ProblemResponse create(ProblemRequest request) {
        if (problemRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Problem with slug already exists: " + request.getSlug());
        }
        Problem problem = Problem.builder()
                .slug(request.getSlug())
                .title(request.getSlug()) // Fallback title
                .description(request.getDescription())
                .difficulty(request.getDifficulty())
                .cpuLimitMs(request.getCpuLimitMs() != null ? request.getCpuLimitMs() : 1000)
                .memoryLimitKb(request.getMemoryLimitKb() != null ? request.getMemoryLimitKb() : 256000)
                .starterCode("")
                .build();

        problem = problemRepository.save(problem);
        return toResponse(problem);
    }

    @Override
    @Transactional
    public ProblemResponse update(Long id, ProblemRequest request) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found: " + id));

        problem.setSlug(request.getSlug());
        problem.setDescription(request.getDescription());
        problem.setDifficulty(request.getDifficulty());

        if (request.getCpuLimitMs() != null) problem.setCpuLimitMs(request.getCpuLimitMs());
        if (request.getMemoryLimitKb() != null) problem.setMemoryLimitKb(request.getMemoryLimitKb());

        problem = problemRepository.save(problem);
        return toResponse(problem);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!problemRepository.existsById(id)) {
            throw new IllegalArgumentException("Problem not found: " + id);
        }
        problemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public TestCase addTestCase(Long problemId, TestCaseRequest request) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found: " + problemId));

        TestCase testCase = TestCase.builder()
                .problem(problem)
                // ✅ FIX: DTO uses 'inputUrl', Entity uses 'input'
                .input(request.getInput())
                // ✅ FIX: DTO uses 'outputUrl', Entity uses 'expectedOutput'
                .expectedOutput(request.getExpectedOutput())
                // ✅ FIX: Null check for boolean
                .isHidden(request.getIsHidden() != null ? request.getIsHidden() : false)
                .build();

        return testCaseRepository.save(testCase);
    }

    private ProblemResponse toResponse(Problem problem) {
        List<ProblemResponse.TestCaseInfo> testCaseInfos = problem.getTestCases().stream()
                .map(tc -> ProblemResponse.TestCaseInfo.builder()
                        .id(tc.getId())
                        .isHidden(tc.getIsHidden())
                        .build())
                .collect(Collectors.toList());

        return ProblemResponse.builder()
                .id(problem.getId())
                .slug(problem.getSlug())
                .description(problem.getDescription())
                .difficulty(problem.getDifficulty())
                .cpuLimitMs(problem.getCpuLimitMs())
                .memoryLimitKb(problem.getMemoryLimitKb())
                .testCases(testCaseInfos)
                // ✅ FIX: Handle null starter code
                .starterCode(problem.getStarterCode() != null ? problem.getStarterCode() : getStarterCode(problem.getSlug()))
                .build();
    }

    private String getStarterCode(String slug) {
        return """
            // Starter code for %s
            public class Main {
                public static void main(String[] args) {
                    // Your code here
                }
            }
            """.formatted(slug);
    }
    @Override
    @Transactional(readOnly = true)
    public List<TestCaseResponse> getTestCases(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found: " + problemId));

        return problem.getTestCases().stream()
                .map(tc -> TestCaseResponse.builder()
                        .id(tc.getId())
                        .input(tc.getInput())
                        .expectedOutput(tc.getExpectedOutput())
                        .isHidden(tc.getIsHidden() != null ? tc.getIsHidden() : false)
                        .build())
                .collect(Collectors.toList());
    }
}