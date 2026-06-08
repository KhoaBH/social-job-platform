package vn.edu.uit.socialjob.platform.modules.company.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import vn.edu.uit.socialjob.platform.common.enums.CompanyRole;
import vn.edu.uit.socialjob.platform.modules.company.dto.CompanyUserRequest;
import vn.edu.uit.socialjob.platform.modules.company.entity.Company;
import vn.edu.uit.socialjob.platform.modules.company.entity.CompanyUser;
import vn.edu.uit.socialjob.platform.modules.company.repository.CompanyUserRepository;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.service.UserService;

@Service
public class CompanyUserService {
    private final CompanyUserRepository companyUserRepository;

    private final UserService userService;

    private final CompanyService companyService;

    public CompanyUserService(
        CompanyUserRepository companyUserRepository,
        UserService userService,
        CompanyService companyService
    ) {
        this.companyUserRepository = companyUserRepository;
        this.userService = userService;
        this.companyService = companyService;
    }

    public List<User> getUsersByCompanyId(UUID companyId) {
        return companyUserRepository.findUsersByCompanyId(companyId);
    } 
    public List<CompanyUser> getCompanyUsersByCompanyId(UUID companyId) {
        return companyUserRepository.findByCompanyId(companyId);
    }
    public CompanyUser create(UUID actorId, CompanyUserRequest companyUser, String companyId) {
        Company company = companyService.getById(UUID.fromString(companyId));
        CompanyUser actorMembership = companyUserRepository
            .findByCompanyIdAndUserId(company.getId(), actorId)
            .orElseThrow(() -> new IllegalArgumentException("Only company owner can add members"));

        if (actorMembership.getRole() != CompanyRole.OWNER) {
            throw new IllegalArgumentException("You don't have permission to add members to this company");
        }

        if (companyUser.getRole() == CompanyRole.OWNER) {
            throw new IllegalArgumentException("Cannot assign OWNER role through member invite");
        }

        User user = userService.getById(companyUser.getUserId());
        CompanyUser newCompanyUser = new CompanyUser();
        newCompanyUser.setUser(user);
        newCompanyUser.setCompany(company);
        newCompanyUser.setRole(companyUser.getRole());


        return companyUserRepository.save(newCompanyUser);
    }
    public CompanyUser createOwner(UUID userId, UUID companyId) {
        Company company = companyService.getById(companyId);
        User user = userService.getById(userId);
        CompanyUser companyUser = new CompanyUser();
        companyUser.setCompany(company);
        companyUser.setUser(user);
        companyUser.setRole(CompanyRole.OWNER);
        return companyUserRepository.save(companyUser);
    }
    public List<CompanyUser> getCompanyByUserId(UUID userId) {
        return companyUserRepository.findByUserId(userId);
    }

    public CompanyUser getCompanyUser(UUID companyId, UUID userId) {
        return companyUserRepository.findByCompanyIdAndUserId(companyId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Company membership not found"));
    }

    public CompanyRole getCompanyRole(UUID companyId, UUID userId) {
        return getCompanyUser(companyId, userId).getRole();
    }

    public boolean canManageRecruitment(UUID companyId, UUID userId) {
        CompanyRole role = getCompanyRole(companyId, userId);
        return role == CompanyRole.OWNER || role == CompanyRole.MANAGER;
    }

    public boolean canManageMembers(UUID companyId, UUID userId) {
        return getCompanyRole(companyId, userId) == CompanyRole.OWNER;
    }
}
