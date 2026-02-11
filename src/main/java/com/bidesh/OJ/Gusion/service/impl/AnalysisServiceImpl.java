package com.bidesh.OJ.Gusion.service.impl;

import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bidesh.OJ.Gusion.dto.ai.AnalysisResponse;
import com.bidesh.OJ.Gusion.entity.Submission;
import com.bidesh.OJ.Gusion.repository.AIAnalysisRepository;
import com.bidesh.OJ.Gusion.repository.SubmissionRepository;
import com.bidesh.OJ.Gusion.service.AnalysisService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final SubmissionRepository submissionRepository;
    private final AIAnalysisRepository aiAnalysisRepository;
    private final ChatClient.Builder chatClientBuilder;

    public AnalysisServiceImpl(SubmissionRepository submissionRepository,
                               AIAnalysisRepository aiAnalysisRepository,
                               @Autowired(required = false) ChatClient.Builder chatClientBuilder) {
        this.submissionRepository = submissionRepository;
        this.aiAnalysisRepository = aiAnalysisRepository;
        this.chatClientBuilder = chatClientBuilder != null ? chatClientBuilder : null;
    }

    private static final String SEMANTIC_VERDICT_SYSTEM_PROMPT = """
            You are a Senior Algorithm Engineer. A student has submitted code that failed specific test cases.
            **Task:** Analyze the provided User Code. Do NOT reveal the correct code.
            **Goal:** Identify the *logical flaw* or *edge case* they missed.
            **Tone:** Encouraging but technical. Use terms like 'off-by-one', 'integer overflow', or 'boundary condition'.
            Respond in 2-4 concise sentences.
            """;

    private static final String COMPLEXITY_SYSTEM_PROMPT = """
            You are a Code Efficiency Expert.
            **Task:** Estimate the Time and Space complexity of the provided code using Big O notation.
            **Output format:** JSON `{ "time": "O(N)", "space": "O(1)", "advice": "..." }`.
            """;

    @Override
    public AnalysisResponse getAnalysis(UUID submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found: " + submissionId));

        // Return cached analysis if exists
        var cached = aiAnalysisRepository.findBySubmissionId(submissionId);
        if (cached.isPresent()) {
            var a = cached.get();
            return AnalysisResponse.builder()
                    .feedback(a.getSemanticFeedback())
                    .complexity(a.getComplexity())
                    .suggestion(a.getSuggestion())
                    .build();
        }

        String problemDesc = submission.getProblem().getDescription();
        String userCode = submission.getCode();

        String userPrompt = """
                **Context:** The problem asks for: %s
                
                **User Code:**
                ```
                %s
                ```
                
                Provide semantic feedback explaining why the logic might have failed (without giving the solution).
                """.formatted(problemDesc != null ? problemDesc : "N/A", userCode);

        try {
            if (chatClientBuilder == null) {
                return getMockAnalysis();
            }
            ChatClient chatClient = chatClientBuilder.build();
            String feedback = chatClient.prompt()
                    .system(SEMANTIC_VERDICT_SYSTEM_PROMPT)
                    .user(userPrompt)
                    .call()
                    .content();

            String complexityPrompt = "Analyze this code:\n```\n%s\n```".formatted(userCode);
            String complexityResp = chatClient.prompt()
                    .system(COMPLEXITY_SYSTEM_PROMPT)
                    .user(complexityPrompt)
                    .call()
                    .content();

            var analysis = com.bidesh.OJ.Gusion.entity.AIAnalysis.builder()
                    .submission(submission)
                    .semanticFeedback(feedback)
                    .complexity(extractComplexity(complexityResp))
                    .suggestion("Try reviewing edge cases and boundary conditions.")
                    .build();
            aiAnalysisRepository.save(analysis);

            return AnalysisResponse.builder()
                    .feedback(feedback)
                    .complexity(extractComplexity(complexityResp))
                    .suggestion(analysis.getSuggestion())
                    .build();
        } catch (Exception e) {
            log.warn("AI analysis failed, returning mock response: {}", e.getMessage());
            return getMockAnalysis();
        }
    }

    @Override
    public Flux<String> streamHint(Long problemId, int level) {
        // Mock streaming for now - in production, use chatClient.stream().content()
        String hint;
        switch (Math.min(level, 3)) {
            case 1 -> hint = "Consider the boundary conditions when the input is empty or has a single element.";
            case 2 -> hint = "Your loop might be off-by-one. Check if you need to include or exclude the last index.";
            case 3 -> hint = "Pseudo-code: Initialize result = 0. For each element, if condition matches, update result. Return result.";
            default -> hint = "Think about the problem constraints and what data structure fits best.";
        }
        return Flux.just(hint).flatMap(s -> Flux.fromArray(s.split("")));
    }

    private String extractComplexity(String response) {
        if (response == null) return "O(N)";
        if (response.contains("\"time\":")) {
            int start = response.indexOf("\"time\":") + 8;
            int end = response.indexOf("\"", start);
            if (end > start) return response.substring(start, end);
        }
        return response.length() > 50 ? response.substring(0, 50) + "..." : response;
    }

    private AnalysisResponse getMockAnalysis() {
        return AnalysisResponse.builder()
                .feedback("You may have missed an edge case. Consider what happens when the input is empty or contains duplicate values. Check for off-by-one errors in your loop indices.")
                .complexity("O(N)")
                .suggestion("Try using a HashMap for O(1) lookups, or verify your boundary conditions.")
                .build();
    }
}
