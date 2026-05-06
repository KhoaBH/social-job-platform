package vn.edu.uit.socialjob.platform.modules.jobpost.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.uit.socialjob.platform.common.enums.ExperienceLevel;
import vn.edu.uit.socialjob.platform.common.enums.JobPostStatus;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobPostRequest {

    @NotNull(message = "Company is required")
    private UUID companyId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private ExperienceLevel experienceLevel;

    private Integer salaryMin;

    private Integer salaryMax;

    private String location;

    private JobPostStatus status;
}