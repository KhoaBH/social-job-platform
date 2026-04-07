package vn.edu.uit.socialjob.platform.modules.skill.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import vn.edu.uit.socialjob.platform.common.entity.BaseEntity;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;

@Getter
@Setter
@Entity
@Table(name = "user_skills", uniqueConstraints = {
    @jakarta.persistence.UniqueConstraint(name = "uk_user_skills_user_skill", columnNames = {"user_id", "skill_id"})
})

public class UserSkill extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false)
    private int level = 1;
    
}
