package com.charan.job_recommender.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String location;
    private Integer yearsOfExperience;
}
