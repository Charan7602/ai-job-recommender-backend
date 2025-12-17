package com.charan.job_recommender.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateResumeRequest {
    @NotNull
    private Long userId;
    @NotBlank
    private String rawText;
    @NotBlank
    private String primaryRole;
    @NotBlank
    private String techStackSummary;
}
