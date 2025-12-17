package com.charan.job_recommender.controller;


import com.charan.job_recommender.dto.CreateJobPostingRequest;
import com.charan.job_recommender.dto.JobPostingResponse;
import com.charan.job_recommender.service.JobPostingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobPostingController {
    private final JobPostingService jobPostingService;
    @PostMapping
    public ResponseEntity<JobPostingResponse> createJob(@Valid @RequestBody CreateJobPostingRequest jobPostingRequest){
        JobPostingResponse response = jobPostingService.CreateJobPosting(jobPostingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostingResponse> getJob(@PathVariable Long id){
        return ResponseEntity.ok(jobPostingService.getJobById(id));
    }

    @GetMapping
    public ResponseEntity<List<JobPostingResponse>> searchJobs(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false, name = "title") String titleKeyword
    ){
        List<JobPostingResponse> results = jobPostingService.searchJobs(location,minExperience,maxExperience,titleKeyword);
        return ResponseEntity.ok(results);
    }
}
