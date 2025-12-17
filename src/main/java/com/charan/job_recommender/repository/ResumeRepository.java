package com.charan.job_recommender.repository;

import com.charan.job_recommender.domain.resume.Resume;
import com.charan.job_recommender.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume,Long> {
    Optional<Resume> findByUser(User user);
}
