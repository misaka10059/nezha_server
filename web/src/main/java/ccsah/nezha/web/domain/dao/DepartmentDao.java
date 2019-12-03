package ccsah.nezha.web.domain.dao;

import ccsah.nezha.web.domain.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *  Created by Rexxar.Zh on 19/11/8
 */
public interface DepartmentDao extends JpaRepository<Department, String> {
    Department findByName(String name);

    List<Department> findByIsDeleted(boolean isDelete);

    List<Department> findByIsDeletedOrderByOrderIndexAsc(boolean isDelete);

}
