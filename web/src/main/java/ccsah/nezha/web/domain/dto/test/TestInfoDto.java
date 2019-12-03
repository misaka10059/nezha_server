package ccsah.nezha.web.domain.dto.test;

import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by Rexxar.Zh on 19/10/11
 */
@Getter
@Setter
public class TestInfoDto implements Serializable {
    @ApiModelProperty(value = "用户名")
    private String user;
    @ApiModelProperty(value = "用户id")
    private String id;


    public TestInfoDto(String user, String id) {
        this.user = user;
        this.id = id;
    }
}
