package vn.edu.uit.socialjob.platform.modules.school.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import vn.edu.uit.socialjob.platform.common.entity.BaseEntity;

@Getter
@Setter
@Entity
@Table(
    name = "schools",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_schools_name", columnNames = "name")
    }
)
public class School extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "logo_url", nullable = true)
    private String logoUrl;

    @Column(nullable = true)
    private String website;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
