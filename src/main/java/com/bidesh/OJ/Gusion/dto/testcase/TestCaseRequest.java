package com.bidesh.OJ.Gusion.dto.testcase;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TestCaseRequest {

    @NotBlank(message = "Input cannot be blank")
    private String input;

    @NotBlank(message = "Expected output cannot be blank")
    private String expectedOutput;

    @NotNull(message = "Hidden flag must be provided")
    @JsonProperty("isHidden")
    @JsonAlias("hidden")
    private Boolean isHidden = false; // Added default value just to be safe
}