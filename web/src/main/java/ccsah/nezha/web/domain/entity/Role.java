package ccsah.nezha.web.domain.entity;

import ccsah.nezha.web.domain.knowledge.RoleTypes;
import ccsfr.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;


@Entity
@Table
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private RoleTypes name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "role")
    private List<RoleAuthority> authorityList;

    public static Role create(RoleTypes name) {
        Role role = new Role();
        role.setName(name);
        session().persist(role);
        return role;
    }
}
