package vn.edu.uit.socialjob.platform.modules.company.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRequest {
    @NotBlank(message = "Company name is required")
    private String name;

    private String logoUrl;

    private String website;

    private Boolean verified;
}
