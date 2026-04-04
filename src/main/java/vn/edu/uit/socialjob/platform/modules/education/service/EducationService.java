package vn.edu.uit.socialjob.platform.modules.education.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.education.dto.EducationRequest;
import vn.edu.uit.socialjob.platform.modules.education.entity.Education;
import vn.edu.uit.socialjob.platform.modules.education.entity.FieldOfStudy;
import vn.edu.uit.socialjob.platform.modules.education.repository.EducationRepository;
import vn.edu.uit.socialjob.platform.modules.school.entity.School;
import vn.edu.uit.socialjob.platform.modules.school.service.SchoolService;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EducationService {

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private FieldOfStudyService fieldOfStudyService;

    public List<Education> getAll() {
        return educationRepository.findAll();
    }

    public Education getById(UUID id) {
        return educationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Education not found"));
    }

    public List<Education> getByUser(UUID userId) {
        return educationRepository.findByUserId(userId);
    }

    public Set<UUID> getSchoolIdsByUser(UUID userId) {

        return educationRepository.findByUserId(userId)
                .stream()
                .map(Education::getSchool)
                .filter(Objects::nonNull)
                .map(School::getId)
                .collect(Collectors.toSet());
    }

    public Set<UUID> getUsersBySchool(UUID schoolId) {

        return educationRepository.findBySchool_Id(schoolId)
                .stream()
                .map(e -> e.getUser().getId())
                .collect(Collectors.toSet());
    }

    public Education create(UUID userId, EducationRequest data) {
        validateYearRange(data.getStartYear(), data.getEndYear());
        validateSchoolInput(data.getSchoolId(), data.getSchoolName());

        User user = userService.getById(userId);
        School school = resolveSchool(data.getSchoolId());
        FieldOfStudy fieldOfStudy = fieldOfStudyService.getById(data.getFieldOfStudyId());

        Education education = new Education();
        education.setUser(user);
        education.setSchool(school);
        education.setSchoolName(resolveSchoolName(school, data.getSchoolName()));
        education.setDegree(data.getDegree());
        education.setFieldOfStudy(fieldOfStudy);
        education.setStartYear(data.getStartYear());
        education.setEndYear(data.getEndYear());

        return educationRepository.save(education);
    }

    public Education update(UUID id, UUID actorId, EducationRequest data) {
        validateYearRange(data.getStartYear(), data.getEndYear());
        validateSchoolInput(data.getSchoolId(), data.getSchoolName());

        Education education = getById(id);
        validateEducationOwner(education, actorId);

        School school = resolveSchool(data.getSchoolId());
        FieldOfStudy fieldOfStudy = fieldOfStudyService.getById(data.getFieldOfStudyId());
        
        education.setSchool(school);
        education.setSchoolName(resolveSchoolName(school, data.getSchoolName()));
        education.setDegree(data.getDegree());
        education.setFieldOfStudy(fieldOfStudy);
        education.setStartYear(data.getStartYear());
        education.setEndYear(data.getEndYear());

        return educationRepository.save(education);
    }

    public void delete(UUID id, UUID actorId) {
        Education education = getById(id);
        validateEducationOwner(education, actorId);
        education.setDeleted(true);
        educationRepository.save(education);
    }

    private void validateEducationOwner(Education education, UUID actorId) {
        if (!education.getUser().getId().equals(actorId)) {
            throw new IllegalArgumentException("You can only modify your own education");
        }
    }

    private void validateYearRange(Integer startYear, Integer endYear) {
        if (startYear != null && endYear != null && endYear < startYear) {
            throw new IllegalArgumentException("End year must be greater than or equal to start year");
        }
    }

    private void validateSchoolInput(UUID schoolId, String schoolName) {
        String normalizedSchoolName = normalize(schoolName);
        if (schoolId == null && normalizedSchoolName == null) {
            throw new IllegalArgumentException("School ID or school name is required");
        }
    }

    private School resolveSchool(UUID schoolId) {
        if (schoolId == null) {
            return null;
        }
        return schoolService.getById(schoolId);
    }

    private String resolveSchoolName(School school, String schoolName) {
        if (school != null) {
            return school.getName();
        }
        return normalize(schoolName);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
