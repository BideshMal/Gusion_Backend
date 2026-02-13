package com.bidesh.OJ.Gusion.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidesh.OJ.Gusion.dto.submission.HistoryResponse;
import com.bidesh.OJ.Gusion.dto.submission.StatusResponse;
import com.bidesh.OJ.Gusion.dto.submission.SubmitRequest;
import com.bidesh.OJ.Gusion.dto.submission.SubmitResponse;
import com.bidesh.OJ.Gusion.service.SubmissionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/submit")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;
   

    @PostMapping
    public ResponseEntity<SubmitResponse> submit(
            @RequestHeader(value = "X-User-Id", required = false) UUID userId,
            @Valid @RequestBody SubmitRequest request) {
        
        // Fallback for testing without Auth token
        if (userId == null) {
            userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        }
        return ResponseEntity.ok(submissionService.submit(userId, request));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<StatusResponse> getStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(submissionService.getStatus(id));
    }

    @GetMapping("/history")
    public ResponseEntity<List<HistoryResponse>> getUserHistory(
            @RequestHeader(value = "X-User-Id", defaultValue = "00000000-0000-0000-0000-000000000001") UUID userId) {
        // ✅ CORRECT: Delegating to Service
        return ResponseEntity.ok(submissionService.getUserHistory(userId));
    }
    // Inside SubmissionController.java

@GetMapping("/problem/{problemId}/last-code")
public ResponseEntity<String> getLastSavedCode(
        @PathVariable Long problemId,
        @RequestHeader(value = "X-User-Id", required = false) UUID userId) {
    
    // Fallback for testing without Auth token (matching your submit logic)
    if (userId == null) {
        userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    }
    
    String savedCode = submissionService.getLastSavedCode(problemId, userId);
    
    // Return the code, or a 204 No Content if they've never submitted before
    if (savedCode.isEmpty()) {
        return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(savedCode);
}
}
