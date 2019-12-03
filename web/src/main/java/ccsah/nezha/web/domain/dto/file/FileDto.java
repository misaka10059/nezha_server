package ccsah.nezha.web.domain.dto.file;

import ccsah.nezha.web.domain.knowledge.AdjudicateTypes;
import ccsfr.core.util.RandomUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

/**
 * Created by Rexxar.Zh on 19/10/11
 */
@Getter
@Setter
public class FileDto implements Serializable {
    private String fileId;
    private String fileName;
    private String fileNumber;
    private String defaultPrefix = "";
    private String ownerName;
    private String ownerDepartment;
    private String adjudicatorName;
    private String adjudicatorDepartment;
    private String reworkHint;
    private List<DocumentDto> documentList;
    private AdjudicateTypes adjudicateState;
    private long uploadTime;
    private long approveTime;

    public static FileDto instance() {
        FileDto instance = new FileDto();
        int magicNumber = RandomUtil.getRandom().ints(1, 1, 10000).toArray()[0];
        instance.setFileId(UUID.randomUUID().toString());
        instance.setFileName("设计文档" + magicNumber);
        instance.setDefaultPrefix("SJWD-");
        instance.setFileNumber("SJWD20190101-" + magicNumber);
        instance.setOwnerName("张三");
        instance.setOwnerDepartment("咨询院");
        instance.setAdjudicatorName("李四");
        instance.setAdjudicatorDepartment("电子档案中心");
        instance.setReworkHint("内容需要完善");
        instance.setDocumentList(DocumentDto.instances(2));
        instance.setUploadTime(Instant.now().toEpochMilli());
        instance.setApproveTime(Instant.now().toEpochMilli());
        return instance;
    }

    public static List<FileDto> instances(int volume) {

        List<FileDto> instances = new ArrayList<>();
        for (int i = 0; i < volume; i++) {
            instances.add(instance());
        }
        return instances;
    }
}
