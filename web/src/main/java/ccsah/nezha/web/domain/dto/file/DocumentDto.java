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
public class DocumentDto implements Serializable {
    private String documentId;
    private String documentName;
    private String documentUrl;
    private long uploadTime;

    public DocumentDto() {
    }

    public DocumentDto(String id, String documentName, String documentUrl, long time) {
        this.documentId = id;
        this.documentName = documentName;
        this.documentUrl = documentUrl;
        this.uploadTime = time;
    }

    public static DocumentDto instance() {
        DocumentDto instance = new DocumentDto();
        instance.setDocumentId(UUID.randomUUID().toString());
        instance.setDocumentName("文档名");
        instance.setDocumentUrl("文档路径");
        instance.setUploadTime(Instant.now().toEpochMilli());
        return instance;
    }

    public static List<DocumentDto> instances(int volume) {

        List<DocumentDto> instances = new ArrayList<>();
        for (int i = 0; i < volume; i++) {
            instances.add(instance());
        }
        return instances;
    }
}
