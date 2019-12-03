package ccsah.nezha.web.component;

import ccsah.nezha.web.configuration.ServiceSettings;
import ccsfr.core.web.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

/**
 * Created by rexxar on 16-7-28.
 */
@Slf4j
@Component
@EnableScheduling
public class FileHolder implements InitializingBean {

    private String filePathRoot = "";
    private int filePathDepth;
    private int filePathVolume;
    private int[] fileIndex;

    @Autowired
    ServiceSettings serviceSettings;

    @Autowired
    RootFolderManager rootFolderManager;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("====================init file holder");

        filePathDepth = serviceSettings.getFilePathDepth();
        filePathVolume = serviceSettings.getFilePathVolume();
        fileIndex = new int[filePathDepth + 1];

        loadFilePathRoot();
        filePathLocate(0);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void setFolderOfTheDay() {
        //建立当天存储目录
        log.info("建立本日目录");
        filePathLocate(0);
    }

    private void loadFilePathRoot() {
        filePathRoot = rootFolderManager.getFilePath() + File.separator + "received-files";
    }

    //定位目前已经记录的索引
    private void filePathLocate(int depth) {
        String path = getIndexPath(depth);
        File rootPath = new File(path);
        rootPath.mkdirs();
        String[] files = rootPath.list();
        int volume = files.length;
        int index = volume == 0 ? 0 : volume - 1;
        fileIndex[depth] = index;
        if (depth < filePathDepth) {
            filePathLocate(depth + 1);
        }
    }

    private String getNewFileLocation() {
        for (int i = filePathDepth; i > 0; i--) {
            fileIndex[i]++;
            if (fileIndex[i] < filePathVolume) {
                String path = getIndexPath(filePathDepth);
                if (i != filePathDepth) {
                    new File(path).mkdirs();
                }
                return getIndexPath(filePathDepth + 1);
            }
            fileIndex[i] = 0;
        }
        String path = getIndexPath(filePathDepth);
        new File(path).mkdirs();
        return path + File.separator + "0";
    }

    private String getIndexPath(int depth) {
        String path = filePathRoot + File.separator + LocalDate.now().toString();
        for (int i = 0; i < depth; i++) {
            path += File.separator + fileIndex[i];
        }
        return path;
    }

    public String saveFile(MultipartFile file) {
        if (file == null || file.getOriginalFilename().equals("kong.log")) {
            return null;
        }
        String fileName = getNewFileLocation() + "-" + file.getOriginalFilename();
        try {
            file.transferTo(new File(fileName));
        } catch (IOException e) {
            log.warn("文件存储失败");
            e.printStackTrace();
            throw new ServiceException(540,"文件存储失败");
        } catch (Exception e) {
            //空指针异常
            log.warn("空指针异常？");
            e.printStackTrace();
            throw new ServiceException(541,"存储异常");
        }
        return fileName;
    }

    public File getFile(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
        } catch (Exception e) {
            log.warn("未读到指定文件" + filePath);
            throw new ServiceException(542,"未读到指定文件" + filePath);
        }
        return file;
    }

    public String getCount() {
        String ref = "";
        for (int aFileIndex : fileIndex) {
            ref += aFileIndex;
            ref += " ";
        }
        return ref;
    }

    public ResponseEntity<InputStreamResource> getFileResponse(String fileUrl) {
        FileSystemResource file = new FileSystemResource(fileUrl);

        if (!file.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache,no-store,must-revalidate");
        String fileName = "attachment;filename=" + file.getFilename();
        headers.add("Content-Disposition", fileName);
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        long contentLength = 0;
        InputStream inputStream = null;
        try {
            contentLength = file.contentLength();
            inputStream = file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] subStrings = fileUrl.split("\\.");
        String extendName = subStrings[subStrings.length - 1];
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        switch (extendName) {
            case "BMP":
            case "bmp":
            case "JPG":
            case "jpg":
            case "PNG":
            case "png":
                mediaType = MediaType.IMAGE_JPEG;
                break;
        }

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(contentLength)
                .contentType(mediaType)
                .body(new InputStreamResource(inputStream));
    }


}
