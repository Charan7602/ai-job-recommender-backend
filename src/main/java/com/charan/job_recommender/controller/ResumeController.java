package com.charan.job_recommender.controller;

import com.charan.job_recommender.dto.CreateResumeRequest;
import com.charan.job_recommender.dto.ResumeResponse;
import com.charan.job_recommender.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;
    @PostMapping
    public ResponseEntity<ResumeResponse> createResume(@Valid @RequestBody CreateResumeRequest request){
        ResumeResponse response = resumeService.CreateResume(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResumeResponse> getResume(@PathVariable Long id){
        return ResponseEntity.ok(resumeService.getResumeById(id));
    }
}
