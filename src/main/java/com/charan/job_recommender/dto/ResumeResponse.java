package com.charan.job_recommender.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResumeResponse {
    private Long id;
    private Long userId;
    private String rawText;
    private String primaryRole;
    private String techStackSummary;
}
