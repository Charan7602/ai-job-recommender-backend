package com.charan.job_recommender.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateJobPostingRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String company;
    @NotBlank
    private String location;
    @NotNull
    @Min(0)
    private Integer minExperience;
    @NotNull
    @Min(0)
    private Integer maxExperience;
    @NotBlank
    private String jobDescription;
}
