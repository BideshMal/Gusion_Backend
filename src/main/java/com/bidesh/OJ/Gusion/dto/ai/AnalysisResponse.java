package com.bidesh.OJ.Gusion.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResponse {
    private String feedback;
    private String complexity;
    private String suggestion;
    // 🟢 Added for multi-level hinting
    private String hintLevel1;
    private String hintLevel2;
    private String hintLevel3;
}