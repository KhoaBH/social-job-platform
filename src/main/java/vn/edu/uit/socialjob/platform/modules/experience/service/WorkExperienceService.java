package vn.edu.uit.socialjob.platform.modules.experience.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.company.entity.Company;
import vn.edu.uit.socialjob.platform.modules.company.service.CompanyService;
import vn.edu.uit.socialjob.platform.modules.experience.dto.WorkExperienceRequest;
import vn.edu.uit.socialjob.platform.modules.experience.entity.WorkExperience;
import vn.edu.uit.socialjob.platform.modules.experience.repository.WorkExperienceRepository;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkExperienceService {

    @Autowired
    private WorkExperienceRepository workExperienceRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    public List<WorkExperience> getAll() {
        return workExperienceRepository.findAll();
    }

    public WorkExperience getById(UUID id) {
        return workExperienceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Work experience not found"));
    }

    public List<WorkExperience> getByUser(UUID userId) {
        return workExperienceRepository.findByUserId(userId);
    }

    public Set<UUID> getCompanyIdsByUser(UUID userId) {

        return workExperienceRepository.findByUserId(userId)
                .stream()
                .map(WorkExperience::getCompany)
                .filter(Objects::nonNull)
                .map(Company::getId)
                .collect(Collectors.toSet());
    }

    public Set<UUID> getUsersByCompany(UUID companyId) {

        return workExperienceRepository.findByCompany_Id(companyId)
                .stream()
                .map(w -> w.getUser().getId())
                .collect(Collectors.toSet());
    }

    public WorkExperience create(UUID userId, WorkExperienceRequest data) {
        validateDateRange(data.getStartDate(), data.getEndDate());
        validateCompanyInput(data.getCompanyId(), data.getCompanyName());

        User user = userService.getById(userId);
        Company company = resolveCompany(data.getCompanyId());

        WorkExperience experience = new WorkExperience();
        experience.setUser(user);
        experience.setCompany(company);
        experience.setCompanyName(resolveCompanyName(company, data.getCompanyName()));
        experience.setJobTitle(data.getJobTitle());
        experience.setStartDate(data.getStartDate());
        experience.setEndDate(data.getEndDate());
        experience.setDescription(data.getDescription());

        return workExperienceRepository.save(experience);
    }

    public WorkExperience update(UUID id, UUID actorId, WorkExperienceRequest data) {
        validateDateRange(data.getStartDate(), data.getEndDate());
        validateCompanyInput(data.getCompanyId(), data.getCompanyName());

        WorkExperience experience = getById(id);
        validateExperienceOwner(experience, actorId);

        Company company = resolveCompany(data.getCompanyId());
        experience.setCompany(company);
        experience.setCompanyName(resolveCompanyName(company, data.getCompanyName()));
        experience.setJobTitle(data.getJobTitle());
        experience.setStartDate(data.getStartDate());
        experience.setEndDate(data.getEndDate());
        experience.setDescription(data.getDescription());

        return workExperienceRepository.save(experience);
    }

    public void delete(UUID id, UUID actorId) {
        WorkExperience experience = getById(id);
        validateExperienceOwner(experience, actorId);
        experience.setDeleted(true);
        workExperienceRepository.save(experience);
    }

    private void validateExperienceOwner(WorkExperience experience, UUID actorId) {
        if (!experience.getUser().getId().equals(actorId)) {
            throw new IllegalArgumentException("You can only modify your own work experience");
        }
    }

    private void validateDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be greater than or equal to start date");
        }
    }

    private void validateCompanyInput(UUID companyId, String companyName) {
        String normalizedCompanyName = normalize(companyName);
        if (companyId == null && normalizedCompanyName == null) {
            throw new IllegalArgumentException("Company ID or company name is required");
        }
    }

    private Company resolveCompany(UUID companyId) {
        if (companyId == null) {
            return null;
        }
        return companyService.getById(companyId);
    }

    private String resolveCompanyName(Company company, String companyName) {
        if (company != null) {
            return company.getName();
        }
        return normalize(companyName);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
