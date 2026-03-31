package vn.edu.uit.socialjob.platform.modules.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.uit.socialjob.platform.modules.education.entity.Education;

import java.util.List;
import java.util.UUID;

public interface EducationRepository extends JpaRepository<Education, UUID> {
    List<Education> findByUserId(UUID userId);
}
