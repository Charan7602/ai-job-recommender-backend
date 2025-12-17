package com.charan.job_recommender.service;

import com.charan.job_recommender.dto.OpenAIEmbeddingRequest;
import com.charan.job_recommender.dto.OpenAIEmbeddingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.List;


@Service
public class EmbeddingService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.embedding.url}")
    private String embeddingUrl;

    @Value("${openai.embedding.model}")
    private String model;


    public String generateEmbeddingAsJson(String text){
        if(text == null || text.isBlank()){
            return null;
        }

        OpenAIEmbeddingRequest requestBody = new OpenAIEmbeddingRequest(model,text);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<OpenAIEmbeddingRequest> request = new HttpEntity<>(requestBody,headers);
        ResponseEntity<OpenAIEmbeddingResponse> response = restTemplate.postForEntity(embeddingUrl,request, OpenAIEmbeddingResponse.class);

        List<Double> embedding = response.getBody().getData().get(0).getEmbedding();

        try {
            return objectMapper.writeValueAsString(embedding);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize embedding", e);
        }
    }
}
