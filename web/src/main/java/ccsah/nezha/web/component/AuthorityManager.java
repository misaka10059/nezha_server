package ccsah.nezha.web.component;

import ccsah.nezha.web.domain.dto.user.AuthorityDto;
import ccsah.nezha.web.domain.entity.User;
import ccsah.nezha.web.domain.knowledge.AuthorityTypes;
import ccsfr.core.web.ServiceException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by rexxar on 19-11-7.
 */
@Component
public class AuthorityManager implements InitializingBean {

    @Autowired
    RedisTemplate redisTemplate;

    private RedisTemplate<String, AuthorityDto> template;


    public String register(User user) {
        String token = UUID.randomUUID().toString();
        AuthorityDto authorityDto = new AuthorityDto(user.getId(),
                user.getRole().getAuthorityList()
                        .stream()
                        .map(a -> a.getAuthority().getName())
                        .collect(Collectors.toList()));
        register(token, authorityDto);
        return token;
    }

    public String register(String token, AuthorityDto authority) {
        redisTemplate.opsForValue().set(token, authority, 30, TimeUnit.MINUTES);
        return token;
    }

    public String getUserId(String token) {
        validateTokenExist(token);
        return template.opsForValue().get(token).getUserId();
    }

//    public void validateOwner(String token, String userId) {
//        if (!userId.equals(getUserId(token))) {
//            throw new ServiceException(411, "");
//        }
//    }

    public void validateAuthority(String token, AuthorityTypes authority) {
        validateTokenExist(token);
        if (!template.opsForValue().get(token)
                .getAuthorityList().contains(authority.name())) {
            throw new ServiceException(411, "用户不具备此权限");
        }

    }

    private void validateTokenExist(String token) {
        if (!redisTemplate.hasKey(token)) {
            throw new ServiceException(410, "用户登录超时，请重新登录");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        template = redisTemplate;
    }
}
