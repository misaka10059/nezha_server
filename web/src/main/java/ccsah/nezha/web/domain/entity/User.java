package ccsah.nezha.web.domain.entity;

import ccsah.nezha.web.util.ValidateUserInfo;
import ccsfr.core.domain.BaseEntity;
import ccsfr.core.util.EncryptionUtil;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


import javax.persistence.*;


@Entity
@Table
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class User extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn
    private Role role;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn
    private Department department;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private int usernumber;

    @Column
    private String email;

    @Column
    private String contact;

    public boolean validatePassword(String password) {
        return EncryptionUtil.equalsPassword(this.getId(), password, this.getPassword());
    }

    public static User create(String username, String password, String contact, String email, Department department, Role role) {
        User user = new User();
        user.setUsername(username);
        user.encodePassword(password);
        user.setDepartment(department);
        user.setRole(role);
        user.setContact(contact);
        user.setEmail(email);
        session().persist(user);
        return user;
    }

    public void encodePassword(String password) {
        this.setPassword(EncryptionUtil.getSaltedPassword(this.getId(), password));
    }


    public String getDisplayUsernumber() {
        return ValidateUserInfo.getDisplayUsernumber(this.usernumber);
    }
}
