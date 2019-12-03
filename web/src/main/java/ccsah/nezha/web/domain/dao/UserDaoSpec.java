package ccsah.nezha.web.domain.dao;

import ccsah.nezha.web.domain.entity.Department;
import ccsah.nezha.web.domain.entity.Role;
import ccsah.nezha.web.domain.entity.User;
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
public class UserDaoSpec {

    public static Specification<User> getVariableSpec(String userName, Department department, long startRegisterTime, long endRegisterTime, Role role) {
        return (entity, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.isFalse(entity.get("isDeleted"));
            List<Predicate> additionList = new ArrayList<>();

            if (!StringUtil.isNullOrEmpty(userName)) {
                additionList.add(criteriaBuilder.like(entity.get("username"), "%" + userName + "%"));
            }
            if (department != null) {
                additionList.add(criteriaBuilder.equal(entity.get("department"), department));
            }
            if (role != null) {
                additionList.add(criteriaBuilder.equal(entity.get("role"), role));
            }
            if (startRegisterTime > 0) {
                Timestamp startTime = Timestamp.from(Instant.ofEpochMilli(startRegisterTime));
                additionList.add(criteriaBuilder.greaterThan(entity.get("ctime"), startTime));
            }
            if (endRegisterTime > 0) {
                Timestamp endTime = Timestamp.from(Instant.ofEpochMilli(endRegisterTime));
                additionList.add(criteriaBuilder.lessThan(entity.get("ctime"), endTime));
            }

            for (Predicate addition : additionList) {
                predicate = criteriaBuilder.and(predicate, addition);
            }
            return predicate;
        };
    }
}
