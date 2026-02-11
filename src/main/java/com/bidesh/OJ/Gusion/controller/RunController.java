package com.bidesh.OJ.Gusion.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidesh.OJ.Gusion.dto.submission.SubmitRequest;
import com.bidesh.OJ.Gusion.entity.Problem;
import com.bidesh.OJ.Gusion.entity.Submission;
import com.bidesh.OJ.Gusion.entity.SubmissionStatus;
import com.bidesh.OJ.Gusion.repository.ProblemRepository;
import com.bidesh.OJ.Gusion.service.JudgeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/run")
@RequiredArgsConstructor
public class RunController {

    private final JudgeService judgeService;
    private final ProblemRepository problemRepository;

    @PostMapping
    public ResponseEntity<Map<String, String>> runCode(@Valid @RequestBody SubmitRequest request) {
        // 1. Fetch Problem
        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new IllegalArgumentException("Problem not found"));

        // 2. Create Temp Submission
        Submission tempSubmission = Submission.builder()
                .id(UUID.randomUUID())
                .problem(problem)
                .code(request.getCode())
                // ✅ FIX: Use the helper method to get the Enum
                .language(request.getRealLanguage()) 
                .status(SubmissionStatus.PENDING)
                .build();

        // 3. Use Custom Input if provided, else Default
        String input = request.getCustomInput();
        if (input == null || input.isBlank()) {
            input = "1 2"; 
        }

        // 4. Run RAW (No verdict, just output)
        String output = judgeService.runRaw(tempSubmission, input);

        // 5. Return Output
        return ResponseEntity.ok(Map.of("output", output));
    }
}