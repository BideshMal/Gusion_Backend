package com.bidesh.OJ.Gusion.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidesh.OJ.Gusion.dto.leaderboard.LeaderboardEntry;
import com.bidesh.OJ.Gusion.repository.SubmissionRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final SubmissionRepository submissionRepository;

    @GetMapping
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard() {
        return ResponseEntity.ok(submissionRepository.findTopUsers());
    }
}