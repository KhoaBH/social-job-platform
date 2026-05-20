package vn.edu.uit.socialjob.platform.modules.jobpost.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobPostWithSkillsRequest extends JobPostRequest {

    private List<JobSkillRequest> skills;
}
