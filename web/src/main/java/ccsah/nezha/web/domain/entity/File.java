package ccsah.nezha.web.domain.entity;

import ccsah.nezha.web.domain.knowledge.AdjudicateTypes;
import ccsfr.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;


@Entity
@Table
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class File extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn
    private User owner;

    @Column
    private String fileName;

    @Column
    private String fileNumber;

    @Column
    private String ownerName;

    @Column
    private String ownerNumber;

    @Column
    private String ownerDepartment;

    @Column
    private String adjudicatorName;

    @Column
    private String adjudicatorNumber;

    @Column
    private String adjudicatorDepartment;

    @Enumerated(EnumType.STRING)
    private AdjudicateTypes adjudicateState;

    @Column
    private String reworkHint;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "file")
    private List<Document> documentList;

    @Column
    private Timestamp uploadTime;

    @Column
    private Timestamp approveTime;

    public static File create(User owner) {
        File file = new File();
        file.setOwner(owner);
        file.setOwnerName(owner.getUsername());
        file.setOwnerDepartment(owner.getDepartment().getName());
        file.setFileName("");
        file.setFileNumber("");
        file.setOwnerNumber("");
        file.setAdjudicatorName("");
        file.setAdjudicatorDepartment("");
        file.setAdjudicatorNumber("");
        file.setAdjudicateState(AdjudicateTypes.NONE);
        file.setReworkHint("");
        file.setUploadTime(Timestamp.from(Instant.ofEpochMilli(0)));
        file.setApproveTime(Timestamp.from(Instant.ofEpochMilli(0)));
        session().persist(file);
        return file;
    }

}
