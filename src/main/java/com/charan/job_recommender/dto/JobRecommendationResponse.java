package com.charan.job_recommender.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobRecommendationResponse {
    private Long id;
    private String title;
    private String company;
    private String location;
    private int experienceScore;
    private int titleScore;
    private int locationScore;
    private int totalScore;
    private String reason;
    private String explanation;
}
