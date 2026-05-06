package vn.edu.uit.socialjob.platform.modules.jobpost.service;

import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.jobpost.dto.JobSkillRequest;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobPost;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobSkill;
import vn.edu.uit.socialjob.platform.modules.jobpost.repository.JobPostRepository;
import vn.edu.uit.socialjob.platform.modules.jobpost.repository.JobSkillRepository;
import vn.edu.uit.socialjob.platform.modules.skill.entity.Skill;
import vn.edu.uit.socialjob.platform.modules.skill.repository.SkillRepository;

import java.util.List;
import java.util.UUID;

@Service
public class JobSkillService {

    private final JobSkillRepository jobSkillRepository;
    private final JobPostRepository jobPostRepository;
    private final SkillRepository skillRepository;

    public JobSkillService(
        JobSkillRepository jobSkillRepository,
        JobPostRepository jobPostRepository,
        SkillRepository skillRepository
    ) {
        this.jobSkillRepository = jobSkillRepository;
        this.jobPostRepository = jobPostRepository;
        this.skillRepository = skillRepository;
    }

    public List<JobSkill> getByJobPostId(UUID jobPostId) {
        return jobSkillRepository.findByJobPostId(jobPostId);
    }

    public JobSkill create(UUID jobPostId, JobSkillRequest data) {
        JobPost jobPost = jobPostRepository.findById(jobPostId)
            .orElseThrow(() -> new IllegalArgumentException("Job post not found"));
        Skill skill = skillRepository.findById(data.getSkillId())
            .orElseThrow(() -> new IllegalArgumentException("Skill not found"));

        JobSkill jobSkill = jobSkillRepository.findByJobPostIdAndSkillId(jobPostId, data.getSkillId())
            .orElseGet(JobSkill::new);

        jobSkill.setJobPost(jobPost);
        jobSkill.setSkill(skill);
        jobSkill.setRequired(data.getRequired() == null || data.getRequired());
        jobSkill.setDeleted(false);

        return jobSkillRepository.save(jobSkill);
    }

    public void delete(UUID id) {
        JobSkill jobSkill = jobSkillRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Job skill not found"));
        jobSkill.setDeleted(true);
        jobSkillRepository.save(jobSkill);
    }
}