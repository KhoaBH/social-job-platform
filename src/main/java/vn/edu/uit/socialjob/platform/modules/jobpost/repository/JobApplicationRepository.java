package vn.edu.uit.socialjob.platform.modules.jobpost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobApplication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {

    @Query("SELECT ja FROM JobApplication ja WHERE ja.jobPost.id = :jobPostId AND ja.isDeleted = false")
    List<JobApplication> findByJobPostId(@Param("jobPostId") UUID jobPostId);

    @Query("SELECT ja FROM JobApplication ja WHERE ja.applicant.id = :userId AND ja.isDeleted = false")
    List<JobApplication> findByApplicantId(@Param("userId") UUID userId);

    @Query("SELECT ja FROM JobApplication ja WHERE ja.jobPost.id = :jobPostId AND ja.applicant.id = :userId AND ja.isDeleted = false")
    Optional<JobApplication> findByJobPostIdAndApplicantId(@Param("jobPostId") UUID jobPostId, @Param("userId") UUID userId);

    @Query("SELECT ja FROM JobApplication ja WHERE ja.id = :id AND ja.isDeleted = false")
    Optional<JobApplication> findById(@Param("id") UUID id);
}