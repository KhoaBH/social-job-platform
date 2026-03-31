package vn.edu.uit.socialjob.platform.modules.experience.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkExperienceRequest {
    private UUID companyId;

    private String companyName;

    private String jobTitle;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;
}
