package vn.edu.uit.socialjob.platform.modules.skill.entity;


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
@Table(name = "skill_categories")
@EqualsAndHashCode(callSuper = false)
public class SkillCategory extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "text",nullable = false)
    private String description;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;
}
