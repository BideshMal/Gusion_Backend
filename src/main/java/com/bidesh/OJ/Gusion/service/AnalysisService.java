package com.bidesh.OJ.Gusion.service;

import java.util.UUID;

import com.bidesh.OJ.Gusion.dto.ai.AnalysisResponse;

/**
 * AI-powered analysis service for failed submissions.
 * Generates semantic feedback, complexity analysis, and progressive hints.
 */
public interface AnalysisService {

    /**
     * Generates AI analysis for a failed submission (Semantic Verdict).
     * Explains logical flaws without revealing the correct solution.
     */
    AnalysisResponse getAnalysis(UUID submissionId);

    /**
     * Streams progressive hints (Conceptual -> Logic -> Pseudo-code) token-by-token via Flux.
     */
    reactor.core.publisher.Flux<String> streamHint(Long problemId, int level);
}
