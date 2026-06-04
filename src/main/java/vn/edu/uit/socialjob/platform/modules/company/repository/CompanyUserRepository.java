package vn.edu.uit.socialjob.platform.modules.company.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.uit.socialjob.platform.modules.company.entity.CompanyUser;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;


public interface CompanyUserRepository extends JpaRepository<CompanyUser, UUID> {
    @Query("select cu.user from CompanyUser cu where cu.company.id = :companyId")
    List<User> findUsersByCompanyId(@Param("companyId") UUID companyId);
    List<CompanyUser> findByCompanyId(UUID companyId);
    java.util.Optional<CompanyUser> findByCompanyIdAndUserId(UUID companyId, UUID userId);
    List<CompanyUser> findByUserId(UUID userId);
}
