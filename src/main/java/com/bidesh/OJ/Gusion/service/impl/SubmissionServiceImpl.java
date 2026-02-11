package com.bidesh.OJ.Gusion.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bidesh.OJ.Gusion.dto.submission.HistoryResponse;
import com.bidesh.OJ.Gusion.dto.submission.StatusResponse;
import com.bidesh.OJ.Gusion.dto.submission.SubmitRequest;
import com.bidesh.OJ.Gusion.dto.submission.SubmitResponse;
import com.bidesh.OJ.Gusion.entity.Problem;
import com.bidesh.OJ.Gusion.entity.Submission;
import com.bidesh.OJ.Gusion.entity.SubmissionStatus;
import com.bidesh.OJ.Gusion.entity.User;
import com.bidesh.OJ.Gusion.entity.UserRole;
import com.bidesh.OJ.Gusion.entity.Verdict;
import com.bidesh.OJ.Gusion.repository.ProblemRepository;
import com.bidesh.OJ.Gusion.repository.SubmissionRepository;
import com.bidesh.OJ.Gusion.repository.UserRepository;
import com.bidesh.OJ.Gusion.service.JudgeService;
import com.bidesh.OJ.Gusion.service.SubmissionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;
    private final JudgeService judgeService;
    private final UserRepository userRepository;

    @Override
    public SubmitResponse submit(UUID userId, SubmitRequest request) {
        log.info("Received submission for User: {}", userId);

        try {
            // 1. Fetch Problem
            Problem problem = problemRepository.findById(request.getProblemId())
                    .orElseThrow(() -> new RuntimeException("Problem not found"));

            // 2. Resolve User (With Retry for Race Conditions)
            User user = resolveUser(userId);

            // 3. Create Submission
            Submission submission = Submission.builder()
                    .id(UUID.randomUUID())
                    .problem(problem)
                    .user(user)
                    .code(request.getCode())
                    .language(request.getRealLanguage())
                    .status(SubmissionStatus.PENDING)
                    .submittedAt(LocalDateTime.now())
                    .build();

            // 4. Save Submission
            Submission savedSubmission = submissionRepository.save(submission);
            UUID submissionId = savedSubmission.getId();

            // 5. Run Judging in Background (ASYNC)
            CompletableFuture.runAsync(() -> runAsyncJudging(submissionId));

            return new SubmitResponse(
                    savedSubmission.getId(),
                    "PENDING",
                    null,
                    0L, 0
            );

        } catch (Exception e) {
            log.error("CRITICAL ERROR in submit():", e);
            throw new RuntimeException("Submission failed: " + e.getMessage());
        }
    }

    /**
     * Tries to find the user. If missing, attempts to create.
     * If creation fails (race condition), it retries the find.
     */
    private User resolveUser(UUID userId) {
        // 1. Atomic Database Insert (PostgreSQL handles the collision)
        String autoEmail = "auto_" + userId.toString().substring(0, 8) + "@gusion.app";
        userRepository.insertUserSafe(userId, autoEmail, "STUDENT");

        // 2. Simple Fetch (Guaranteed to be there now)
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found after insert"));
    }

    // Isolated method to attempt creation
    private User createUserSafe(UUID userId) {
        String autoEmail = "auto_" + userId.toString().substring(0, 8) + "@gusion.app";
        // Check email first (Double check)
        if (userRepository.existsByEmail(autoEmail)) {
            return userRepository.findByEmail(autoEmail).orElseThrow();
        }

        User newUser = User.builder()
                .id(userId)
                .email(autoEmail)
                .role(UserRole.STUDENT)
                .build();

        // This might throw DataIntegrityViolation or OptimisticLockingFailure
        // if another thread inserts at the same time. The loop above handles it.
        return userRepository.save(newUser);
    }

    private void runAsyncJudging(UUID submissionId) {
        try {
            Submission submission = submissionRepository.findById(submissionId)
                    .orElseThrow(() -> new RuntimeException("Submission lost in async"));

            Verdict verdict = judgeService.judge(submission);

            submission.setVerdict(verdict);
            submission.setStatus(SubmissionStatus.COMPLETED);
            submissionRepository.save(submission);

            log.info("Judging finished for {}: {}", submissionId, verdict);

        } catch (Exception e) {
            log.error("Async Judging Failed for " + submissionId, e);
            try {
                Submission s = submissionRepository.findById(submissionId).orElse(null);
                if (s != null) {
                    s.setStatus(SubmissionStatus.COMPLETED);
                    s.setVerdict(Verdict.RE);
                    submissionRepository.save(s);
                }
            } catch (Exception ex) { /* Ignore */ }
        }
    }

    @Override
    public StatusResponse getStatus(UUID submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        return new StatusResponse(
                submission.getStatus().toString(),
                submission.getVerdict() != null ? submission.getVerdict().toString() : null
        );
    }

    @Override
    public List<HistoryResponse> getUserHistory(UUID userId) {
        return submissionRepository.findByUserIdOrderBySubmittedAtDesc(userId).stream()
                .limit(10)
                .map(s -> new HistoryResponse(
                        s.getId(),
                        s.getProblem().getTitle(),
                        s.getVerdict() != null ? s.getVerdict().toString() : "PENDING",
                        s.getLanguage().toString(),
                        0.0,
                        s.getSubmittedAt()
                ))
                .collect(Collectors.toList());
    }
}