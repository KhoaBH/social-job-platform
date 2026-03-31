package vn.edu.uit.socialjob.platform.modules.company.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import vn.edu.uit.socialjob.platform.modules.company.dto.CompanyRequest;
import vn.edu.uit.socialjob.platform.modules.company.entity.Company;
import vn.edu.uit.socialjob.platform.modules.company.service.CompanyService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping
    public ResponseEntity<List<Company>> listAll() {
        return ResponseEntity.ok(companyService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Company> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(companyService.getById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Company>> getByOwner(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(companyService.getByOwner(ownerId));
    }

    @PostMapping
    public ResponseEntity<Company> create(@Valid @RequestBody CompanyRequest data, Authentication authentication) {
        UUID ownerId = extractUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(companyService.create(ownerId, data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Company> update(
        @PathVariable UUID id,
        @Valid @RequestBody CompanyRequest data,
        Authentication authentication
    ) {
        UUID actorId = extractUserId(authentication);
        return ResponseEntity.ok(companyService.update(id, actorId, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        UUID actorId = extractUserId(authentication);
        companyService.delete(id, actorId);
        return ResponseEntity.noContent().build();
    }

    private UUID extractUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}
