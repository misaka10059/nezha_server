package ccsah.nezha.web.service;

import ccsah.nezha.web.component.AuthorityManager;
import ccsah.nezha.web.domain.dao.*;
import ccsah.nezha.web.domain.dto.file.DocumentDto;
import ccsah.nezha.web.domain.dto.file.FileDto;
import ccsah.nezha.web.domain.entity.Document;
import ccsah.nezha.web.domain.entity.File;
import ccsah.nezha.web.domain.entity.User;
import ccsah.nezha.web.domain.knowledge.AdjudicateTypes;
import ccsfr.core.domain.PageOffsetRequest;
import ccsfr.core.util.StringUtil;
import ccsfr.core.util.TimeUtil;
import ccsfr.core.web.PageData;
import ccsfr.core.web.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Rexxar.Zh on 19/10/11
 */
@Service
@Slf4j
public class FileService {

    @Autowired
    RoleDao roleDao;

    @Autowired
    UserDao userDao;

    @Autowired
    FileDao fileDao;

    @Autowired
    DocumentDao documentDao;

    @Autowired
    AuthorityManager authorityManager;

    @Autowired
    UserService userService;

    public FileDto getFileDtoById(String id) {
        File file = getFile(id);
        return getFileDto(file);
    }

    @Transactional
    public FileDto sign(String userToken) {
        User user = getUserByToken(userToken);
        File file = File.create(user);
        FileDto fileDto = getFileDto(file);
        fileDto.setDefaultPrefix(user.getDepartment().getCode());
        return fileDto;
    }


    public PageData<FileDto> list(String operatorToken, String fileName, String fileNumber, String ownerName, String ownerNumber, String department, long startTime, long endTime, AdjudicateTypes adjudicateType, Pageable pageRequest) {

        User user = getUserByToken(operatorToken);

        if (false) {//填入不具有广域查询权限
            if (!StringUtil.isNullOrEmpty(ownerNumber) &&
                    !ownerNumber.equals(user.getDisplayUsernumber())) {
                throw new ServiceException(570, "用户权限不足");
            }
            ownerNumber = user.getDisplayUsernumber();
        }


        Specification<File> querySpec = FileDaoSpec.getVariableSpec(
                fileName,
                fileNumber,
                ownerName,
                ownerNumber,
                department,
                startTime,
                endTime,
                adjudicateType);

        Page<File> filePage = fileDao.findAll(querySpec, pageRequest);
        List<FileDto> fileList = filePage.getContent().stream()
                .map(this::getFileDto)
                .collect(Collectors.toList());
        return new PageData<>(fileList, (int) filePage.getTotalElements());

    }

    private User getUserByToken(String userToken) {
        String userId = authorityManager.getUserId(userToken);
        return userDao.findOne(userId);
    }

    @Transactional
    public FileDto update(String userToken, String fileId, String fileName, String fileNumber, String reworkHint, AdjudicateTypes adjudicateType) {
        User operator = getUserByToken(userToken);
        File file = getFile(fileId);
        switch (adjudicateType) {

            case PENDING://PENDING为用户操作，提交信息同时更新标签，表示此文档已经待管理员审核
                file.setUploadTime(TimeUtil.currentTimestamp());
                file.setAdjudicateState(AdjudicateTypes.PENDING);

            case NONE://NONE为用户操作,表示不对文档状态做出改变，仅更新信息
                if (!file.getFileName().equals(fileName)) {
                    validateFileName(fileName);
                    file.setFileName(fileName);
                }
                if (!file.getFileNumber().equals(fileNumber)) {
                    validateFileNumber(fileNumber);
                    file.setFileNumber(fileNumber);
                }
                if (file.getAdjudicateState() == AdjudicateTypes.NONE) {
                    file.setAdjudicateState(AdjudicateTypes.DRAFT);
                }
                break;

            case APPROVE://APPROVE为管理员操作表明文档通过审核
                if (!file.getAdjudicateState().equals(AdjudicateTypes.PENDING)) {
                    throw new ServiceException(571, "仅可审核待审核文档");
                }
                file.setAdjudicatorName(operator.getUsername());
                file.setAdjudicatorNumber(operator.getDisplayUsernumber());
                file.setAdjudicatorDepartment(operator.getDepartment().getName());
                file.setReworkHint("");
                file.setApproveTime(TimeUtil.currentTimestamp());
                file.setAdjudicateState(AdjudicateTypes.APPROVE);
                break;

            case REJECT://REJECT为管理员操作，需要填写ReworkHint
                if (!file.getAdjudicateState().equals(AdjudicateTypes.PENDING)) {
                    throw new ServiceException(571, "仅可审核待审核文档");
                }
                file.setAdjudicatorName(operator.getUsername());
                file.setAdjudicatorNumber(operator.getDisplayUsernumber());
                file.setAdjudicatorDepartment(operator.getDisplayUsernumber());
                if (StringUtil.isNullOrEmpty(reworkHint)) {
                    throw new ServiceException(516, "需要提供修改建议");
                }
                file.setReworkHint(reworkHint);
                file.setAdjudicateState(AdjudicateTypes.REJECT);
                break;
        }

        return getFileDto(file);
    }

    @Transactional
    public FileDto uploadDocument(String fileId, String fileName, String fileLink) {
        File file = getFile(fileId);
        file.getDocumentList().add(Document.create(file, fileName, fileLink));
        return getFileDto(file);
    }

    @Transactional
    public FileDto deleteDocument(String documentId) {
        Document document = getDocument(documentId);
        File file = document.getFile();
        file.getDocumentList().remove(document);
        document.deletePhysical();
        return getFileDto(file);
    }

    @Transactional
    public void deleteDocumentMultiple(List<String> documentIdList) {
        documentIdList.forEach(id -> getDocument(id).deletePhysical());
    }

    @Transactional
    public FileDto recoverFile(String fileId) {
        File file = getFile(fileId);
        file.recoverLogicalDelete();
        return getFileDto(file);
    }

    @Transactional
    public void recoverFileMultiple(List<String> fileIdList) {
        fileIdList.forEach(id -> getFile(id).recoverLogicalDelete());
    }

    @Transactional
    public FileDto deleteFile(String fileId) {
        File file = getFile(fileId);
        file.deleteLogical();
        return getFileDto(file);
    }

    @Transactional
    public void deleteFileMultiple(List<String> fileIdList) {
        fileIdList.forEach(id -> getFile(id).deleteLogical());
    }

    private FileDto getFileDto(File file) {
        FileDto fileDto = new FileDto();
        fileDto.setFileId(file.getId());
        fileDto.setFileName(file.getFileName());
        fileDto.setFileNumber(file.getFileNumber());
        fileDto.setOwnerName(file.getOwnerName());
        fileDto.setOwnerDepartment(file.getOwnerDepartment());
        fileDto.setAdjudicatorName(file.getAdjudicatorName());
        fileDto.setAdjudicatorDepartment(file.getAdjudicatorDepartment());
        fileDto.setAdjudicateState(file.getAdjudicateState());
        fileDto.setReworkHint(file.getReworkHint());
        if (file.getDocumentList() != null) {
            fileDto.setDocumentList(
                    file.getDocumentList().stream()
                            .map(this::getDocumentDto)
                            .sorted(Comparator.comparing(DocumentDto::getUploadTime))
                            .collect(Collectors.toList()));
        }
        fileDto.setUploadTime(file.getUploadTime().getTime());
        fileDto.setApproveTime(file.getApproveTime().getTime());
        return fileDto;
    }

    private DocumentDto getDocumentDto(Document document) {
        Timestamp ctime = document.getCtime();
        return new DocumentDto(
                document.getId(),
                document.getDocumentName(),
                document.getDocumentUrl(),
                ctime == null ? TimeUtil.currentTimestamp().getTime() : ctime.getTime());
    }


    private void validateFileName(String fileName) {
        if (fileName != null && fileDao.findByFileNameAndIsDeleted(fileName, false) != null) {
            throw new ServiceException(510, "键入的文件名重复");
        }
    }

    private void validateFileNumber(String fileNumber) {
        if (fileNumber != null && fileDao.findByFileNumberAndIsDeleted(fileNumber, false) != null) {
            throw new ServiceException(510, "键入的文件名重复");
        }
    }


    private File getFile(String id) {
        File file = fileDao.findOne(id);
        if (file == null) {
            throw new ServiceException(512, "文档不存在");
        }
        return file;
    }

    private Document getDocument(String id) {
        Document document = documentDao.findOne(id);
        if (document == null) {
            throw new ServiceException(522, "附件不存在");
        }
        return document;
    }

}
