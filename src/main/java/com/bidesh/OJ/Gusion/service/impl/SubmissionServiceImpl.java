package com.bidesh.OJ.Gusion.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
    @Transactional
    public SubmitResponse submit(UUID userId, SubmitRequest request) {
        log.info("Submission initiated - User: {}, Problem: {}", userId, request.getProblemId());

        try {
            Problem problem = problemRepository.findById(request.getProblemId())
                    .orElseThrow(() -> new RuntimeException("Problem not found"));

            User user = resolveUser(userId);

            Submission submission = Submission.builder()
                    .problem(problem)
                    .user(user)
                    .code(request.getCode())
                    .language(request.getRealLanguage())
                    .status(SubmissionStatus.PENDING)
                    .submittedAt(LocalDateTime.now())
                    .build();

            // 🟢 FIX 1: Use saveAndFlush to push to DB immediately
            Submission savedSubmission = submissionRepository.saveAndFlush(submission);
            UUID submissionId = savedSubmission.getId();

            // 🟢 FIX 2: Add a tiny delay to ensure the transaction is committed
            CompletableFuture.runAsync(() -> {
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                runAsyncJudging(submissionId);
            });

            return new SubmitResponse(submissionId, "PENDING", null, 0L, 0);

        } catch (Exception e) {
            log.error("Submission entry failed: {}", e.getMessage());
            throw new RuntimeException("Submission failed");
        }
    }

    private User resolveUser(UUID userId) {
        return userRepository.findById(userId).orElseGet(() -> {
            log.info("New participant {} detected. Auto-registering...", userId);

            String autoEmail = "auto_" + userId.toString().substring(0, 8) + "@gusion.io";
            // Ensure your UserRepository has this updated method to handle name/password!
            userRepository.insertUserSafe(userId, autoEmail, "STUDENT");

            return userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Failed to resolve user session"));
        });
    }

    private void runAsyncJudging(UUID submissionId) {
        try {
            Submission submission = submissionRepository.findById(submissionId)
                    .orElseThrow(() -> new RuntimeException("Critical: Submission ID missing during judging"));

            // 🟢 The judgeService will run all test cases (public + hidden)
            Verdict verdict = judgeService.judge(submission);

            submission.setVerdict(verdict);
            submission.setStatus(SubmissionStatus.COMPLETED);
            submissionRepository.save(submission);

            log.info("Judging result for {}: {}", submissionId, verdict);

        } catch (Exception e) {
            log.error("Async Engine Error for {}: {}", submissionId, e.getMessage());
            markAsError(submissionId);
        }
    }

    private void markAsError(UUID id) {
        submissionRepository.findById(id).ifPresent(s -> {
            s.setStatus(SubmissionStatus.COMPLETED);
            s.setVerdict(Verdict.RE); // Runtime Error as fallback
            submissionRepository.save(s);
        });
    }

    @Override
    public StatusResponse getStatus(UUID submissionId) {
        Submission s = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Status request for unknown ID"));

        return new StatusResponse(
                s.getStatus().toString(),
                s.getVerdict() != null ? s.getVerdict().toString() : null
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
                        // In a full implementation, you'd calculate pass % here
                        s.getVerdict() == Verdict.AC ? 100.0 : 0.0,
                        s.getSubmittedAt()
                ))
                .collect(Collectors.toList());
    }
    // Inside SubmissionServiceImpl.java

@Override
public String getLastSavedCode(Long problemId, UUID userId) {
    return submissionRepository.findFirstByProblemIdAndUserIdOrderBySubmittedAtDesc(problemId, userId)
            .map(Submission::getCode) // If found, return the code
            .orElse(""); // If no previous submission, return an empty string
}
}
