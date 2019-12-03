package ccsah.nezha.web.domain.entity;

import ccsfr.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class Authority extends BaseEntity {

    @Column
    private String name;

    public static Authority create(String name) {
        Authority authority = new Authority();
        authority.setName(name);
        session().persist(authority);
        return authority;
    }
}
