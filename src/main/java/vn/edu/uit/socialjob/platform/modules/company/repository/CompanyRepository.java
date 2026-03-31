package vn.edu.uit.socialjob.platform.modules.company.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.uit.socialjob.platform.modules.company.entity.Company;

import java.util.List;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    List<Company> findByOwnerId(UUID ownerId);
}
