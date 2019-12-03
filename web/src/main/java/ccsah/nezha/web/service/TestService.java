package ccsah.nezha.web.service;

import ccsah.nezha.web.domain.entity.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Created by Rexxar.Zh on 19/10/11
 */
@Service
@Slf4j
public class TestService {
    public String hello() {
        return "Hello";
    }
}
