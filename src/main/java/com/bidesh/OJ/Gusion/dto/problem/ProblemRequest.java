package com.bidesh.OJ.Gusion.dto.problem;

import com.bidesh.OJ.Gusion.entity.Difficulty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProblemRequest {
    @NotBlank
    private String slug;

    private String description;

    @NotNull
    private Difficulty difficulty;

    @Positive
    private Integer cpuLimitMs = 1000;

    @Positive
    private Integer memoryLimitKb = 256000;
}
