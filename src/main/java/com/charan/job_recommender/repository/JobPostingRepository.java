package com.charan.job_recommender.repository;

import com.charan.job_recommender.domain.job.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobPostingRepository extends JpaRepository<JobPosting,Long>, JpaSpecificationExecutor<JobPosting> {
}
