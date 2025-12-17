package com.charan.job_recommender.service;

import com.charan.job_recommender.domain.job.JobPosting;
import com.charan.job_recommender.dto.CreateJobPostingRequest;
import com.charan.job_recommender.dto.JobPostingResponse;
import com.charan.job_recommender.exception.ResourceNotFoundException;
import com.charan.job_recommender.repository.JobPostingRepository;
import com.charan.job_recommender.repository.ResumeRepository;
import com.charan.job_recommender.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobPostingService {
    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final EmbeddingService embeddingService;

    @Transactional
    public JobPostingResponse CreateJobPosting(CreateJobPostingRequest request){
        String embeddingJson = null;
        String semanticText = request.getTitle() + ".  " + request.getJobDescription();
        if(request.getJobDescription() != null && !request.getJobDescription().isBlank()) {
            embeddingJson = embeddingService.generateEmbeddingAsJson(semanticText);
        }

        JobPosting job = JobPosting.builder()
                .title(request.getTitle())
                .company(request.getCompany())
                .location(request.getLocation())
                .minExperience(request.getMinExperience())
                .maxExperience(request.getMaxExperience())
                .jobDescription(request.getJobDescription())
                .embedding(embeddingJson)
                .build();

        JobPosting saved = jobPostingRepository.save(job);
        return JobPostingResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .company(saved.getCompany())
                .location(saved.getLocation())
                .minExperience(saved.getMinExperience())
                .maxExperience(saved.getMaxExperience())
                .jobDescription(saved.getJobDescription())
                .build();
    }

    @Transactional
    public JobPostingResponse getJobById(Long id){
        JobPosting job = jobPostingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        return JobPostingResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .minExperience(job.getMinExperience())
                .maxExperience(job.getMaxExperience())
                .jobDescription(job.getJobDescription())
                .build();
    }

    public List<JobPostingResponse> searchJobs(String location, Integer minExperience, Integer maxExperience, String titleKeyword){
        Specification<JobPosting> spec = (root, query, cb) -> cb.conjunction();
        if(location != null && !location.isBlank()){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
        }

        if(minExperience != null){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("maxExperience"),minExperience));
        }

        if(maxExperience != null){
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("minExperience"),maxExperience));
        }

        if (titleKeyword != null && !titleKeyword.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("title")), "%" + titleKeyword.toLowerCase() + "%"));
        }

        List<JobPosting> jobs = jobPostingRepository.findAll(spec);

        return jobs.stream()
                .map(this::toResponse)
                .toList();
    }

    public List<JobPostingResponse> recommendJobsForUser(Long userId){
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found with id: " + userId));
        var resume = resumeRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found for user id: " + user.getId()));

        String location = user.getLocation();
        Integer yearsOfExperience = user.getYearsOfExperience();
        String primaryRole = resume.getPrimaryRole();

        Specification<JobPosting> spec = (root, query, cb) -> cb.conjunction();
        if(location != null && !location.isBlank()){
            spec = spec.and(((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), "%" + location.toLowerCase() + "%")));
        }

        if(yearsOfExperience != null){
            spec = spec
                    .and(((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("minExperience"),yearsOfExperience)))
                    .and(((root, query, criteriaBuilder) ->
                            criteriaBuilder.greaterThanOrEqualTo(root.get("maxExperience"),yearsOfExperience)));

        }

        if(primaryRole != null && !primaryRole.isBlank()){
            String keyword = primaryRole.toLowerCase();
            spec = spec.and(((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),"%" + keyword + "%")));
        }

        return jobPostingRepository.findAll(spec).stream().map(this::toResponse).toList();
    }

    private JobPostingResponse toResponse(JobPosting job) {
        return JobPostingResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .minExperience(job.getMinExperience())
                .maxExperience(job.getMaxExperience())
                .jobDescription(job.getJobDescription())
                .build();
    }
}
