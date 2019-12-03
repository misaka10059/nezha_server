package ccsah.nezha.web.component;

import ccsah.nezha.web.Application;
import ccsah.nezha.web.domain.dto.user.AuthorityDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;

/**
 * Created by rexxar on 19-11-8.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(Application.class)
public class AuthorityManagerTest {

    @Autowired
    AuthorityManager authorityManager;

    @Test
    public void registerTest() {
        authorityManager.register("test-token", new AuthorityDto("test-id", new ArrayList<>()));

        String id = authorityManager.getUserId("test-token");

        Assert.assertEquals("test-id",id);
    }

}
