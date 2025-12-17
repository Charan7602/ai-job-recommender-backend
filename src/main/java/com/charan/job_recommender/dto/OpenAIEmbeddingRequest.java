package com.charan.job_recommender.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAIEmbeddingRequest {
    private String model;
    private String input;
}
