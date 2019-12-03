package ccsah.nezha.web.domain.dto.user;

import ccsah.nezha.web.domain.knowledge.RoleTypes;
import ccsfr.core.util.TimeUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Rexxar.Zh on 19/10/11
 */
@Getter
@Setter
public class UserDto implements Serializable {

    private String token = "";
    private String userId = "";
    private String userNumber;
    private String username;
    private String contact;
    private String email;
    private String authority;
    private String department;
    private long loginTime;
    private long registerTime;

    public UserDto(String userId, String username, String department, String userNumber, String contact, String email, RoleTypes role, long loginTime, long registerTime) {
        this.setUserId(userId);
        this.setUsername(username);
        this.setDepartment(department);
        this.setUserNumber(userNumber);
        this.setContact(contact);
        this.setEmail(email);
        this.setAuthority(role.name());
        this.setLoginTime(loginTime);
        this.setRegisterTime(registerTime);
    }

    private UserDto(String username, String department) {
        this.setUsername(username);
        this.setDepartment(department);
        this.loginTime = Instant.now().toEpochMilli();
        this.registerTime = Instant.now().toEpochMilli();
    }

    public static UserDto instance() {
        UserDto instance = new UserDto("张三", "咨询院");
        instance.setToken(UUID.randomUUID().toString());
        instance.setUserNumber("23010001");
        instance.setContact("13800001234");
        instance.setEmail("zhangsan@ccsah.com");
        instance.setAuthority("USER");

        return instance;
    }

    public static List<UserDto> instances(int volume) {
        List<UserDto> instances = new ArrayList<>();
        for (int i = 0; i < volume; i++) {
            instances.add(instance());
        }
        return instances;
    }
}
