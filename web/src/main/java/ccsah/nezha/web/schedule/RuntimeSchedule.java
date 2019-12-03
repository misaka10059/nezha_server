package ccsah.nezha.web.schedule;

import ccsah.nezha.web.component.FileHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *  Created by Rexxar.Zh on 19/11/25
 */
@Slf4j
@Component
@EnableScheduling
public class RuntimeSchedule {

    @Autowired
    FileHolder fileHolder;

    /**
     * 每天0点执行一次
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void daily() {

    }


}
