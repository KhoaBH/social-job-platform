package vn.edu.uit.socialjob.platform.modules.education.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vn.edu.uit.socialjob.platform.common.entity.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "field_of_studies")
@EqualsAndHashCode(callSuper = false)
public class FieldOfStudy extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;
}
