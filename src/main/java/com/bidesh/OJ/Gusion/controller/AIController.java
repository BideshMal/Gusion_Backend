package com.bidesh.OJ.Gusion.controller;

import com.bidesh.OJ.Gusion.dto.ai.AnalysisResponse;
import com.bidesh.OJ.Gusion.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AnalysisService analysisService;

    @GetMapping("/analysis/{submissionId}")
    public ResponseEntity<AnalysisResponse> getAnalysis(@PathVariable UUID submissionId) {
        return ResponseEntity.ok(analysisService.getAnalysis(submissionId));
    }

    @GetMapping(value = "/hint/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamHint(
            @RequestParam Long problemId,
            @RequestParam(defaultValue = "1") int level) {
        SseEmitter emitter = new SseEmitter(60_000L);
        analysisService.streamHint(problemId, level)
                .subscribe(
                        chunk -> {
                            try {
                                emitter.send(chunk);
                            } catch (Exception ex) {
                                emitter.completeWithError(ex);
                            }
                        },
                        emitter::completeWithError,
                        emitter::complete
                );
        return emitter;
    }
}