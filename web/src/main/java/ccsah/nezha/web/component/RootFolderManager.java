package ccsah.nezha.web.component;

import ccsah.nezha.web.configuration.ServiceSettings;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by rexxar on 16-7-25.
 */

@Component
@Slf4j
@Getter
public class RootFolderManager implements InitializingBean {

    private String filePath = "";


    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("==================setting up root folder");
        loadFilePath();
        log.info("==================root folder is " + filePath);
    }

    @Autowired
    ServiceSettings serviceSettings;

    /**
     * 设置文件根目录
     * 从路径左侧开始扫描，第一个与rootFolder同名的文件夹作为根目录
     * 如果没找到，在最高级目录下建立根目录
     */
    private void loadFilePath() {
        String rootFolder = serviceSettings.getRootFolder();
        String[] path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("\\\\", "/").split(("/"));
        String filePath = "";
        for (String folder : path) {
            filePath += folder;
            if (folder.equals(rootFolder)) {
                this.filePath = filePath.replace("file:", "");
                return;
            }
            filePath += File.separator;
        }
        filePath = path[0] + File.separator + rootFolder;
        filePath = filePath.replace("file:", "");
        File directory = new File(filePath);
        directory.mkdirs();
        this.filePath = filePath;
    }
}
