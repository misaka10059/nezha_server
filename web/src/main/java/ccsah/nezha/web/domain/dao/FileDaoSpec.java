package ccsah.nezha.web.domain.dao;

import ccsah.nezha.web.domain.entity.File;
import ccsah.nezha.web.domain.knowledge.AdjudicateTypes;
import ccsah.nezha.web.util.ValidateUserInfo;
import ccsfr.core.util.StringUtil;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rexxar.Zh on 19/11/8
 */
public class FileDaoSpec {

    public static Specification<File> getVariableSpec(String fileName, String fileNumber, String ownerName, String ownerNumber, String department, long startTime, long endTime, AdjudicateTypes adjudicateType) {
        return (entity, query, criteriaBuilder) -> {

            Predicate predicate = criteriaBuilder.equal(
                    entity.get("isDeleted"),
                    AdjudicateTypes.DELETED.equals(adjudicateType));

            List<Predicate> additionList = new ArrayList<>();

            if (!StringUtil.isNullOrEmpty(fileName)) {
                additionList.add(criteriaBuilder.like(entity.get("fileName"), "%" + fileName + "%"));
            }
            if (!StringUtil.isNullOrEmpty(fileNumber)) {
                additionList.add(criteriaBuilder.like(entity.get("fileNumber"), "%" + fileNumber + "%"));
            }
            if (!StringUtil.isNullOrEmpty(ownerName)) {
                additionList.add(criteriaBuilder.like(entity.get("ownerName"), "%" + fileName + "%"));
            }
            if (!StringUtil.isNullOrEmpty(ownerNumber)) {
                int inner = ValidateUserInfo.getInnerUsernumber(ownerNumber);
                additionList.add(criteriaBuilder.equal(entity.get("ownerNumber"), inner));
            }
            if (!StringUtil.isNullOrEmpty(department)) {
                additionList.add(criteriaBuilder.equal(entity.get("ownerDepartment"), department));
            }
            if (adjudicateType != null &&
                    adjudicateType != AdjudicateTypes.DELETED &&
                    adjudicateType != AdjudicateTypes.NONE) {
                additionList.add(criteriaBuilder.equal(entity.get("adjudicateState"), adjudicateType));
            } else {
                additionList.add(criteriaBuilder.notEqual(entity.get("adjudicateState"), AdjudicateTypes.NONE));
                additionList.add(criteriaBuilder.notEqual(entity.get("adjudicateState"), AdjudicateTypes.DRAFT));
            }
            if (startTime > 0) {
                Timestamp start = Timestamp.from(Instant.ofEpochMilli(startTime));
                additionList.add(criteriaBuilder.greaterThan(entity.get("uploadTime"), start));
            }
            if (endTime > 0) {
                Timestamp end = Timestamp.from(Instant.ofEpochMilli(endTime));
                additionList.add(criteriaBuilder.lessThan(entity.get("uploadTime"), end));
            }
            for (Predicate addition : additionList) {
                predicate = criteriaBuilder.and(predicate, addition);
            }
            return predicate;
        };
    }

}
