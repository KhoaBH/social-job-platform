package vn.edu.uit.socialjob.platform.modules.education.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.uit.socialjob.platform.common.enums.Degree;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EducationRequest {
    private UUID schoolId;

    private String schoolName;

    @NotNull(message = "Degree is required")
    private Degree degree;

    @NotNull(message = "Field of Study ID is required")
    private UUID fieldOfStudyId;

    private Integer startYear;

    private Integer endYear;
}
