package ccsah.nezha.web.domain.entity;

import ccsfr.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


@Entity
@Table(name="role_authority")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class RoleAuthority extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn
    private Role role;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn
    private Authority authority;

    public static RoleAuthority create(Role role,Authority authority) {
        RoleAuthority roleAuthority = new RoleAuthority();
        roleAuthority.setRole(role);
        roleAuthority.setAuthority(authority);
        session().persist(roleAuthority);
        return roleAuthority;
    }
}
