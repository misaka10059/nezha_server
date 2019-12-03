package ccsah.nezha.web.domain.entity;

import ccsfr.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;


@Entity
@Table
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class Document extends BaseEntity {

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn
    private File file;

    @Column
    private String documentName;

    @Column
    private String documentUrl;

    public static Document create(File file, String name, String url) {
        Document document = new Document();
        document.setFile(file);
        document.setDocumentName(name);
        document.setDocumentUrl(url);
        session().persist(document);
        return document;
    }
}
