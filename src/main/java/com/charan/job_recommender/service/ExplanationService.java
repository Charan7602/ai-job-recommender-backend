package com.charan.job_recommender.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExplanationService {
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.chat.url}")
    private String chatUrl;
    @Value("${openai.chat.model}")
    private String model;

    public String generateExplanation(String resumeText,String jobTitle,String jobDescription){
        if (resumeText == null || resumeText.isBlank()) {
            return null;
        }
        try{
            String prompt = """
                    You are an assistant explaining job recommendations.
                                        
                    Resume summary:
                    %s
                                        
                    Job:
                    Title: %s
                    Description: %s
                                        
                    Explain in 2 short sentences why this job matches the resume.
                    Focus on skills and experience alignment.
                    Avoid generic phrases.
                    """
                    .formatted(resumeText, jobTitle, jobDescription);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String,Object> body = new HashMap<>();
            body.put("model",model);
            body.put("messages",List.of(Map.of("role","user","content",prompt)));


            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(chatUrl,request,Map.class);
            Map firstChoice =
                    (Map) ((List<?>) response.getBody().get("choices")).get(0);

            Map message = (Map) firstChoice.get("message");

            return message.get("content").toString().trim();


        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
