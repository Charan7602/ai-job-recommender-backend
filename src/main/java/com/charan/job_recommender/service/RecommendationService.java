package com.charan.job_recommender.service;

import com.charan.job_recommender.domain.job.JobPosting;
import com.charan.job_recommender.domain.resume.Resume;
import com.charan.job_recommender.domain.user.User;
import com.charan.job_recommender.dto.JobRecommendationResponse;
import com.charan.job_recommender.exception.ResourceNotFoundException;
import com.charan.job_recommender.repository.JobPostingRepository;
import com.charan.job_recommender.repository.ResumeRepository;
import com.charan.job_recommender.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ExplanationService explanationService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final int EXPERIENCE_MAX = 50;
    private static final int TITLE_MAX = 30;
    private static final int LOCATION_MAX = 20;
    private static final double SEMANTIC_THRESHOLD = 0.15;
    private static final int EXPLANATION_LIMIT = 3;

    private JobRecommendationResponse scoreJob(JobPosting job, User user, Resume resume){
        int experienceScore = calculateExperienceScore(job,user);
        int titleScore = calculateSemanticScore(job,resume);
        int locationScore = calculateLocationScore(job,user);
        int totalScore = experienceScore + locationScore + titleScore;
        if(totalScore == 0) return null;
        return JobRecommendationResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .experienceScore(experienceScore)
                .titleScore(titleScore)
                .locationScore(locationScore)
                .totalScore(totalScore)
                .reason(buildReason(experienceScore, titleScore, locationScore))
                .build();
    }

    public Page<JobRecommendationResponse> recommendJobs(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Resume resume = resumeRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        List<JobRecommendationResponse> scored =
                jobPostingRepository.findAll().stream()
                .map(job -> scoreJob(job, user, resume))   // INTERNAL
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(
                        JobRecommendationResponse::getTotalScore).reversed())
                .toList();

        for(int i=0;i<Math.min(EXPLANATION_LIMIT,scored.size());i++)
        {
            JobRecommendationResponse rec = scored.get(i);
            String explanation = explanationService.generateExplanation(
                    resume.getRawText(),
                    rec.getTitle(),
                    jobPostingRepository.findById(rec.getId())
                            .map(JobPosting::getJobDescription)
                            .orElse("")
            );
            rec.setExplanation(explanation);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), scored.size());

        List<JobRecommendationResponse> pageContent = start > end ? List.of() : scored.subList(start,end);
        return new PageImpl<>(pageContent,pageable,scored.size());
    }

    private int calculateExperienceScore(JobPosting job, User user){
        int yoe = user.getYearsOfExperience();
        if(yoe >= job.getMinExperience() && yoe <= job.getMaxExperience())
        {
            return EXPERIENCE_MAX;
        }

        int diff = Math.abs(job.getMinExperience() - yoe);
        if(diff == 1) return 30;
        if(diff == 2) return 15;
        return 0;
    }

    private int calculateTitleScore(JobPosting job, Resume resume){
        String title = job.getTitle().toLowerCase();
        String[] keywords = resume.getPrimaryRole().toLowerCase().split("\\s+");
        int matches = 0;
        for(String word:keywords){
            if(title.contains(word)){
                matches++;
            }
        }
        return Math.min(matches * 15, TITLE_MAX);
    }

    private int calculateLocationScore(JobPosting job, User user){
        if(job.getLocation() == null || user.getLocation() == null) return 0;
        if(job.getLocation().toLowerCase().contains(user.getLocation().toLowerCase())){
            return LOCATION_MAX;
        }
        return 0;
    }

    private String buildReason(int exp, int title, int loc) {
        List<String> reasons = new ArrayList<>();

        if (exp > 0) reasons.add("experience matches");
        if (title > 0) reasons.add("role aligns with your profile");
        if (loc > 0) reasons.add("location matches");

        return String.join(", ", reasons);
    }

    private float[] parseEmbedding(String json){
        try{
            List<Double> list = objectMapper.readValue(json, new TypeReference<>(){});
            float[] vec = new float[list.size()];
            for(int i=0;i<list.size();i++){
                vec[i] = list.get(i).floatValue();
            }
            return vec;
        } catch (Exception e){
            throw new RuntimeException("Failed to parse embedding",e);
        }
    }

    private double cosineSimilarity(float []a, float []b){
        double dot = 0, normA = 0, normB = 0;
        for(int i=0;i<a.length;i++){
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private int calculateSemanticScore(JobPosting job,Resume resume){
        if(job.getEmbedding() == null || resume.getEmbedding() == null){
            return 0;
        }
        float []jobVec = parseEmbedding(job.getEmbedding());
        float []resumeVec = parseEmbedding(resume.getEmbedding());

        double similarity = cosineSimilarity(jobVec,resumeVec);

        if (similarity < SEMANTIC_THRESHOLD) {
            return 0;
        }

        return (int) (similarity * TITLE_MAX);
    }
}
