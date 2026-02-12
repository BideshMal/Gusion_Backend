package com.bidesh.OJ.Gusion.controller;

import java.util.List;
import java.util.Map;

import com.bidesh.OJ.Gusion.dto.testcase.TestCaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidesh.OJ.Gusion.dto.problem.ProblemRequest;
import com.bidesh.OJ.Gusion.dto.problem.ProblemResponse;
import com.bidesh.OJ.Gusion.dto.testcase.TestCaseRequest;
import com.bidesh.OJ.Gusion.entity.TestCase;
import com.bidesh.OJ.Gusion.service.ProblemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping
    public ResponseEntity<Page<ProblemResponse>> listProblems(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(problemService.findAll(pageable));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ProblemResponse> getProblem(@PathVariable String slug) {
        return ResponseEntity.ok(problemService.findBySlug(slug));
    }

    @PostMapping
    public ResponseEntity<ProblemResponse> createProblem(@Valid @RequestBody ProblemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(problemService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProblemResponse> updateProblem(
            @PathVariable Long id,
            @Valid @RequestBody ProblemRequest request) {
        return ResponseEntity.ok(problemService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProblem(@PathVariable Long id) {
        problemService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Problem deleted successfully"));
    }

    @PostMapping("/{id}/testcases")
    public ResponseEntity<TestCase> addTestCase(
            @PathVariable Long id,
            @Valid @RequestBody TestCaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(problemService.addTestCase(id, request));
    }

    @GetMapping("/{id}/testcases")
    public ResponseEntity<List<TestCaseResponse>> getTestCases(@PathVariable Long id) {
        return ResponseEntity.ok(problemService.getTestCases(id));
    }
}
