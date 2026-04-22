package vn.edu.uit.socialjob.platform.modules.company.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

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
    public CompanyUser create(CompanyUserRequest companyUser, String companyId) {
        User user = userService.getById(companyUser.getUserId());
        Company company = companyService.getById(UUID.fromString(companyId));
        CompanyUser newCompanyUser = new CompanyUser();
        newCompanyUser.setUser(user);
        newCompanyUser.setCompany(company);
        newCompanyUser.setRole(companyUser.getRole());


        return companyUserRepository.save(newCompanyUser);
    }
}
