package ccsah.nezha.web.domain.entity;

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
public class Department extends BaseEntity {

    @Column
    private String name;

    @Column
    private String code;

    @Column
    private int orderIndex;

    public static Department create(String name, String code, int order) {

        Department department = new Department();
        department.setName(name);
        department.setCode(code);
        department.setOrderIndex(order);
        session().persist(department);
        return department;
    }
}
