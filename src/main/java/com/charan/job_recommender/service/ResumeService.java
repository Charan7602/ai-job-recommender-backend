package com.charan.job_recommender.service;

import com.charan.job_recommender.domain.resume.Resume;
import com.charan.job_recommender.domain.user.User;
import com.charan.job_recommender.dto.CreateResumeRequest;
import com.charan.job_recommender.dto.ResumeResponse;
import com.charan.job_recommender.exception.ResourceNotFoundException;
import com.charan.job_recommender.repository.ResumeRepository;
import com.charan.job_recommender.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final EmbeddingService embeddingService;

    @Transactional
    public ResumeResponse CreateResume(CreateResumeRequest request){
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("user not found with id: " + request.getUserId()));

        String embeddingJson = null;
        if (request.getRawText() != null && !request.getRawText().isBlank()) {
            embeddingJson = embeddingService.generateEmbeddingAsJson(request.getRawText());
        }

        Resume resume = Resume.builder()
                .user(user)
                .rawText(request.getRawText())
                .primaryRole(request.getPrimaryRole())
                .techStackSummary(request.getTechStackSummary())
                .embedding(embeddingJson)
                .build();
        Resume saved = resumeRepository.save(resume);

        return ResumeResponse.builder()
                .id(saved.getId())
                .userId(saved.getUser().getId())
                .rawText(saved.getRawText())
                .primaryRole(saved.getPrimaryRole())
                .techStackSummary(saved.getTechStackSummary())
                .build();
    }

    @Transactional
    public ResumeResponse getResumeById(Long id){
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with id: " + id));
        return ResumeResponse.builder()
                .id(resume.getId())
                .userId(resume.getUser().getId())
                .rawText(resume.getRawText())
                .primaryRole(resume.getPrimaryRole())
                .techStackSummary(resume.getTechStackSummary())
                .build();
    }
}
