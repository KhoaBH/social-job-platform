package vn.edu.uit.socialjob.platform.modules.company.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.edu.uit.socialjob.platform.common.entity.BaseEntity;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;

@Getter
@Setter
@Entity
@Table(
    name = "companies",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_companies_name", columnNames = "name")
    }
)
public class Company extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    @ToString.Exclude
    private User owner;

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
