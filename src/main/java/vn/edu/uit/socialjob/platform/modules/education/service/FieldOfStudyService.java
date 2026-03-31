package vn.edu.uit.socialjob.platform.modules.education.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.education.dto.FieldOfStudyRequest;
import vn.edu.uit.socialjob.platform.modules.education.entity.FieldOfStudy;
import vn.edu.uit.socialjob.platform.modules.education.repository.FieldOfStudyRepository;

import java.util.List;
import java.util.UUID;

@Service
public class FieldOfStudyService {
    @Autowired
    private FieldOfStudyRepository fieldOfStudyRepository;

    public List<FieldOfStudy> getAll() {
        return fieldOfStudyRepository.findAll();
    }

    public FieldOfStudy getById(UUID id) {
        return fieldOfStudyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Field of study not found"));
    }

    public FieldOfStudy getBySlug(String slug) {
        return fieldOfStudyRepository.findBySlug(slug)
            .orElseThrow(() -> new IllegalArgumentException("Field of study not found"));
    }

    public FieldOfStudy create(FieldOfStudyRequest data) {
        String name = data.getName().trim();
        String slug = name.toLowerCase().replaceAll("\\s+", "-");
        
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setName(name);
        fieldOfStudy.setDescription(data.getDescription() != null ? data.getDescription().trim() : null);
        fieldOfStudy.setSlug(slug);

        return fieldOfStudyRepository.save(fieldOfStudy);
    }

    public FieldOfStudy update(UUID id, FieldOfStudyRequest data) {
        FieldOfStudy fieldOfStudy = this.getById(id);
        String name = data.getName().trim();
        String slug = name.toLowerCase().replaceAll("\\s+", "-");
        
        fieldOfStudy.setName(name);
        fieldOfStudy.setDescription(data.getDescription() != null ? data.getDescription().trim() : null);
        fieldOfStudy.setSlug(slug);

        return fieldOfStudyRepository.save(fieldOfStudy);
    }

    public void delete(UUID id) {
        FieldOfStudy fieldOfStudy = this.getById(id);
        fieldOfStudyRepository.delete(fieldOfStudy);
    }
}
