package vn.edu.uit.socialjob.platform.modules.experience.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.uit.socialjob.platform.modules.experience.entity.WorkExperience;

import java.util.List;
import java.util.UUID;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, UUID> {
    List<WorkExperience> findByUserId(UUID userId);
}
