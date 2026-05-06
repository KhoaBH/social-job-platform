package vn.edu.uit.socialjob.platform.modules.jobpost.service;

import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.common.enums.JobApplicationStatus;
import vn.edu.uit.socialjob.platform.modules.jobpost.dto.JobApplicationRequest;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobApplication;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobPost;
import vn.edu.uit.socialjob.platform.modules.jobpost.repository.JobApplicationRepository;
import vn.edu.uit.socialjob.platform.modules.jobpost.repository.JobPostRepository;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostRepository jobPostRepository;
    private final UserRepository userRepository;

    public JobApplicationService(
        JobApplicationRepository jobApplicationRepository,
        JobPostRepository jobPostRepository,
        UserRepository userRepository
    ) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.jobPostRepository = jobPostRepository;
        this.userRepository = userRepository;
    }

    public List<JobApplication> getByJobPostId(UUID jobPostId) {
        return jobApplicationRepository.findByJobPostId(jobPostId);
    }

    public List<JobApplication> getByApplicantId(UUID userId) {
        return jobApplicationRepository.findByApplicantId(userId);
    }

    public JobApplication getById(UUID id) {
        return jobApplicationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Job application not found"));
    }

    public JobApplication apply(UUID userId, UUID jobPostId, JobApplicationRequest data) {
        JobPost jobPost = jobPostRepository.findById(jobPostId)
            .orElseThrow(() -> new IllegalArgumentException("Job post not found"));
        User applicant = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        JobApplication jobApplication = jobApplicationRepository
            .findByJobPostIdAndApplicantId(jobPostId, userId)
            .orElseGet(JobApplication::new);

        jobApplication.setJobPost(jobPost);
        jobApplication.setApplicant(applicant);
        jobApplication.setAppliedAt(LocalDateTime.now());
        jobApplication.setCoverLetter(data.getCoverLetter());
        jobApplication.setStatus(JobApplicationStatus.APPLIED);
        jobApplication.setDeleted(false);

        return jobApplicationRepository.save(jobApplication);
    }

    public JobApplication updateStatus(UUID id, JobApplicationStatus status) {
        JobApplication jobApplication = getById(id);
        jobApplication.setStatus(status);
        return jobApplicationRepository.save(jobApplication);
    }

    public void delete(UUID id) {
        JobApplication jobApplication = getById(id);
        jobApplication.setDeleted(true);
        jobApplicationRepository.save(jobApplication);
    }
}