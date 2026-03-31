package vn.edu.uit.socialjob.platform.modules.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.school.dto.SchoolRequest;
import vn.edu.uit.socialjob.platform.modules.school.entity.School;
import vn.edu.uit.socialjob.platform.modules.school.repository.SchoolRepository;

import java.util.List;
import java.util.UUID;

@Service
public class SchoolService {

    @Autowired
    private SchoolRepository schoolRepository;

    public List<School> getAll() {
        return schoolRepository.findAll();
    }

    public School getById(UUID id) {
        return schoolRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("School not found"));
    }

    public School create(SchoolRequest data) {
        School school = new School();
        school.setName(data.getName().trim());
        school.setLogoUrl(data.getLogoUrl());
        school.setWebsite(data.getWebsite());
        return schoolRepository.save(school);
    }

    public School update(UUID id, SchoolRequest data) {
        School school = getById(id);
        school.setName(data.getName().trim());
        school.setLogoUrl(data.getLogoUrl());
        school.setWebsite(data.getWebsite());
        return schoolRepository.save(school);
    }

    public void delete(UUID id) {
        School school = getById(id);
        school.setDeleted(true);
        schoolRepository.save(school);
    }
}
