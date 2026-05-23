package vn.edu.uit.socialjob.platform.modules.jobpost.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import vn.edu.uit.socialjob.platform.common.entity.BaseEntity;
import vn.edu.uit.socialjob.platform.common.enums.ExperienceLevel;
import vn.edu.uit.socialjob.platform.common.enums.JobPostStatus;
import vn.edu.uit.socialjob.platform.modules.company.entity.Company;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;

@Getter
@Setter
@Entity
@Table(name = "job_posts")
public class JobPost extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "posted_by_id", nullable = false)
    private User postedBy;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level")
    private ExperienceLevel experienceLevel;

    @Column(name = "salary_min")
    private Integer salaryMin;

    @Column(name = "salary_max")
    private Integer salaryMax;

    @Column
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobPostStatus status = JobPostStatus.DRAFT;

    @Transient
    @JsonProperty("apply_status")
    private Boolean applyStatus = false;

    @Transient
    @JsonProperty("applications")
    private Integer applications = 0;
}