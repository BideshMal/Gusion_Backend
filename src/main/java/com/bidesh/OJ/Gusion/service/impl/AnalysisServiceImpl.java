package com.bidesh.OJ.Gusion.service.impl;

import java.util.UUID;
import org.springframework.ai.chat.client.ChatClient;
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
    private final ChatClient chatClient;

    public AnalysisServiceImpl(SubmissionRepository submissionRepository,
                               AIAnalysisRepository aiAnalysisRepository,
                               ChatClient.Builder chatClientBuilder) {
        this.submissionRepository = submissionRepository;
        this.aiAnalysisRepository = aiAnalysisRepository;
        // 🟢 This initializes the real ChatClient
        this.chatClient = chatClientBuilder.build();
    }

    private static final String SYSTEM_PROMPT = """
            You are an expert Coding Mentor. A student's code has failed.
            You are provided with the Problem Description, their Code, and the Correct Reference Solution.
            **Task:**
            1. Analyze the logical flaw in the user's code.
            2. Provide a 'feedback' summary.
            3. Provide 3 progressive hints:
               - Level 1: Conceptual (no code).
               - Level 2: Logic-based (direction).
               - Level 3: Pseudo-code or specific logic.
            Respond ONLY in JSON format:
            {
              "feedback": "...",
              "complexity": "...",
              "hintLevel1": "...",
              "hintLevel2": "...",
              "hintLevel3": "..."
            }
            """;

    @Override
    public AnalysisResponse getAnalysis(UUID submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        // Check cache first...
        var cached = aiAnalysisRepository.findBySubmissionId(submissionId);
        if (cached.isPresent()) {
            // ... (existing cache logic) ...
            var a = cached.get();
            return AnalysisResponse.builder()
                    .feedback(a.getSemanticFeedback()).complexity(a.getComplexity())
                    .hintLevel1(a.getHintLevel1()).hintLevel2(a.getHintLevel2()).hintLevel3(a.getHintLevel3())
                    .build();
        }

        // Prepare Prompt
        String problemDesc = submission.getProblem().getDescription();
        String userCode = submission.getCode();
        String refSolution = submission.getProblem().getReferenceSolution();

        // 🟢 FORCE RAW JSON IN PROMPT
        String systemPrompt = SYSTEM_PROMPT + "\nIMPORTANT: Return raw JSON only. Do not use Markdown (```json).";

        String userPrompt = String.format("Problem: %s\nUser Code: %s\nReference: %s",
                problemDesc, userCode, refSolution);

        // 🟢 ROBUST CALL WITH STRING CLEANUP
        String rawResponse = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content(); // Get raw string first

        // Clean up markdown if GPT adds it
        if (rawResponse.startsWith("```json")) {
            rawResponse = rawResponse.replace("```json", "").replace("```", "");
        } else if (rawResponse.startsWith("```")) {
            rawResponse = rawResponse.replace("```", "");
        }

        // Parse manually (safer)
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            AnalysisResponse aiResp = mapper.readValue(rawResponse, AnalysisResponse.class);

            saveToDb(submission, aiResp);
            return aiResp;

        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", rawResponse);
            throw new RuntimeException("AI Mentor is confused. Please try again.");
        }
    }

    private void saveToDb(Submission s, AnalysisResponse r) {
        var entity = com.bidesh.OJ.Gusion.entity.AIAnalysis.builder()
                .submission(s)
                .semanticFeedback(r.getFeedback())
                .complexity(r.getComplexity())
                .hintLevel1(r.getHintLevel1())
                .hintLevel2(r.getHintLevel2())
                .hintLevel3(r.getHintLevel3())
                .build();
        aiAnalysisRepository.save(entity);
    }

    @Override
    public Flux<String> streamHint(Long problemId, int level) {
        return Flux.just("Feature coming soon...");
    }
}