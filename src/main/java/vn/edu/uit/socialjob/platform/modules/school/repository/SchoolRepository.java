package vn.edu.uit.socialjob.platform.modules.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.uit.socialjob.platform.modules.school.entity.School;

import java.util.UUID;

public interface SchoolRepository extends JpaRepository<School, UUID> {
}
