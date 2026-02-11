package com.bidesh.OJ.Gusion.dto.problem;

import java.util.List;

import com.bidesh.OJ.Gusion.entity.Difficulty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemResponse {
    private Long id;
    private String slug;
    private String description;
    private Difficulty difficulty;
    private Integer cpuLimitMs;
    private Integer memoryLimitKb;
    private List<TestCaseInfo> testCases;
    private String starterCode; // Template per language (optional)

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCaseInfo {
        private Long id;
        private Boolean isHidden;
        // input/output URLs excluded for hidden cases when user views
    }
}
