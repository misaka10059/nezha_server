package ccsah.nezha.web.domain.dao;

import ccsah.nezha.web.domain.entity.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Rexxar.Zh on 19/11/8
 */
public interface FileDao extends JpaRepository<File, String> {

    File findByFileNameAndIsDeleted(String fileName, boolean b);

    File findByFileNumberAndIsDeleted(String fileNumber, boolean b);

    Page<File> findAll(Specification<File> querySpec, Pageable pageOffsetRequest);
}
