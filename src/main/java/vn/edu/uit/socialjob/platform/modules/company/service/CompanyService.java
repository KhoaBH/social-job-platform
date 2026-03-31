package vn.edu.uit.socialjob.platform.modules.company.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.uit.socialjob.platform.modules.company.dto.CompanyRequest;
import vn.edu.uit.socialjob.platform.modules.company.entity.Company;
import vn.edu.uit.socialjob.platform.modules.company.repository.CompanyRepository;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.service.UserService;

import java.util.List;
import java.util.UUID;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserService userService;

    public List<Company> getAll() {
        return companyRepository.findAll();
    }

    public Company getById(UUID id) {
        return companyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Company not found"));
    }

    public List<Company> getByOwner(UUID ownerId) {
        return companyRepository.findByOwnerId(ownerId);
    }

    public Company create(UUID ownerId, CompanyRequest data) {
        User owner = userService.getById(ownerId);

        Company company = new Company();
        company.setOwner(owner);
        company.setName(data.getName().trim());
        company.setLogoUrl(data.getLogoUrl());
        company.setWebsite(data.getWebsite());
        company.setVerified(Boolean.TRUE.equals(data.getVerified()));

        return companyRepository.save(company);
    }

    public Company update(UUID id, UUID actorId, CompanyRequest data) {
        Company company = getById(id);
        validateCompanyOwner(company, actorId);

        company.setName(data.getName().trim());
        company.setLogoUrl(data.getLogoUrl());
        company.setWebsite(data.getWebsite());
        if (data.getVerified() != null) {
            company.setVerified(data.getVerified());
        }

        return companyRepository.save(company);
    }

    public void delete(UUID id, UUID actorId) {
        Company company = getById(id);
        validateCompanyOwner(company, actorId);
        company.setDeleted(true);
        companyRepository.save(company);
    }

    private void validateCompanyOwner(Company company, UUID actorId) {
        if (!company.getOwner().getId().equals(actorId)) {
            throw new IllegalArgumentException("Only company owner can modify this company");
        }
    }
}
