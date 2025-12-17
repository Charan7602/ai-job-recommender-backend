package com.charan.job_recommender.controller;

import com.charan.job_recommender.domain.resume.Resume;
import com.charan.job_recommender.domain.user.User;
import com.charan.job_recommender.dto.JobPostingResponse;
import com.charan.job_recommender.dto.JobRecommendationResponse;
import com.charan.job_recommender.exception.ResourceNotFoundException;
import com.charan.job_recommender.repository.JobPostingRepository;
import com.charan.job_recommender.repository.ResumeRepository;
import com.charan.job_recommender.repository.UserRepository;
import com.charan.job_recommender.service.JobPostingService;
import com.charan.job_recommender.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    @GetMapping
    public ResponseEntity<Page<JobRecommendationResponse>> recommendJobs(@RequestParam Long userId, Pageable pageable){
        Page<JobRecommendationResponse> results = recommendationService.recommendJobs(userId,pageable);
        return ResponseEntity.ok(results);
    }
}
