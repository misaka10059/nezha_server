package ccsah.nezha.web.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by rexxar on 16-7-25.
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "service")
public class ServiceSettings {

    private String rootFolder;
    private int filePathDepth;
    private int filePathVolume;

}
