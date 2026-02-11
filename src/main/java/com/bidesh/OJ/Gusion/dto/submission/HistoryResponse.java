package com.bidesh.OJ.Gusion.dto.submission;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryResponse {
    private UUID submissionId;
    private String problemTitle;
    private String verdict;
    private String language;
    private Double executionTime; // ✅ The missing field causing the error!
    private LocalDateTime submittedAt;
}