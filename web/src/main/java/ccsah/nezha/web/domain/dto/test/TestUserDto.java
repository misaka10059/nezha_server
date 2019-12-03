package ccsah.nezha.web.domain.dto.test;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Rexxar.Zh on 19/10/11
 */
@Getter
@Setter
public class TestUserDto implements Serializable {
    private String user;
    private String id;
    private String textTime;
    private LocalDateTime time;

    public TestUserDto(String user, String id) {
        this.user = user;
        this.id = id;
        this.time = LocalDateTime.now();
        this.textTime=time.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS"));
    }
}
