package vn.edu.uit.socialjob.platform.modules.education.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.edu.uit.socialjob.platform.common.entity.BaseEntity;
import vn.edu.uit.socialjob.platform.common.enums.Degree;
import vn.edu.uit.socialjob.platform.modules.school.entity.School;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
@Getter
@Setter
@Entity
@Table(name = "educations")
public class Education extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    @ToString.Exclude
    private School school;

    @Column(name = "school_name")
    private String schoolName;

    @Enumerated(EnumType.STRING)
    @Column(name = "degree")
    private Degree degree;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "field_of_study_id", nullable = false)
    @ToString.Exclude
    private FieldOfStudy fieldOfStudy;

    @Column(name = "start_year")
    private Integer startYear;

    @Column(name = "end_year")
    private Integer endYear;
}
