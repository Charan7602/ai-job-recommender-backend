package com.charan.job_recommender.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobPostingResponse {
    private Long id;
    private String title;
    private String company;
    private String location;
    private Integer minExperience;
    private Integer maxExperience;
    private String  jobDescription;
}
