package com.bidesh.OJ.Gusion.dto.testcase;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TestCaseRequest {
    @NotBlank
    private String inputUrl;

    @NotBlank
    private String outputUrl;

    @NotNull
    private Boolean isHidden;
}
