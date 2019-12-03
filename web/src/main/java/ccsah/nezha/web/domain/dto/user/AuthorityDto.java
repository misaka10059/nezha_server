package ccsah.nezha.web.domain.dto.user;

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
public class AuthorityDto implements Serializable {
    private String userId;
    private List<String> authorityList;

    public AuthorityDto(String userId, List<String> authorityList) {
        this.userId = userId;
        this.authorityList = authorityList;
    }
}
