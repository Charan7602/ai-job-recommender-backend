package com.charan.job_recommender.dto;

import lombok.Data;

import java.util.List;

@Data
public class OpenAIEmbeddingResponse {
    private List<EmbeddingData> data;
    @Data
    public static class EmbeddingData {
        private List<Double> embedding;
    }
}
