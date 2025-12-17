package com.charan.job_recommender.domain.job;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_postings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String company;
    private String location;
    @Column(name = "min_experience")
    private Integer minExperience;
    @Column(name = "max_experience")
    private Integer maxExperience;
    @Column(name = "job_description", nullable = false, columnDefinition = "text")
    private String jobDescription;
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Column(columnDefinition = "text")
    private String embedding;
    @PrePersist
    void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }
    @PreUpdate
    void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
