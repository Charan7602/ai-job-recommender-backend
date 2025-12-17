package com.charan.job_recommender.domain.resume;

import com.charan.job_recommender.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true, columnDefinition = "text")
    private User user;
    @Column(name = "raw_text", nullable = false)
    private String rawText;
    private String primaryRole;
    private String techStackSummary;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Column(columnDefinition = "text")
    private String embedding;
    @PrePersist
    void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
