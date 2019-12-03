package ccsah.nezha.web.controller;

import ccsah.nezha.web.component.FileHolder;
import ccsah.nezha.web.domain.dto.file.FileDto;
import ccsah.nezha.web.domain.knowledge.AdjudicateTypes;
import ccsah.nezha.web.service.FileService;
import ccsfr.core.domain.PageOffsetRequest;
import ccsfr.core.web.BaseApiController;
import ccsfr.core.web.PageData;
import ccsfr.core.web.ResponseData;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * Created by Rexxar.Zh on 19/10/31
 */
@Api(value = "/file", description = "文档模块")
@RequestMapping("file")
@RestController
public class FileController extends BaseApiController {

    @Autowired
    FileService fileService;

    @RequestMapping(value = "/sign", method = RequestMethod.POST)
    @ApiOperation("新建一个用于更新信息的空文档记录")
    public ResponseData<FileDto> sign(@RequestParam("operator_token") String userToken) {
        return ResponseData.ok(fileService.sign(userToken));
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value="更新文档信息" ,notes =
            "NONE为用户操作,表示不对文档状态做出改变，仅更新信息   \n" +
            "PENDING为用户操作，提交信息同时更新标签，表示此文档已经待管理员审核  \n" +
            "APPROVE为管理员操作表明文档通过审核  \n" +
            "REJECT为管理员操作，需要填写ReworkHint")
    public ResponseData<FileDto> update(@RequestParam("operator_token") String userToken,
                                        @RequestParam("file_id") String fileId,
                                        @RequestParam("file_name") String fileName,
                                        @RequestParam("file_number") String fileNumber,
                                        @RequestParam(value = "rework_hint", required = false) String reworkHint,
                                        @RequestParam("adjudicate_type") AdjudicateTypes adjudicateType) {
        return ResponseData.ok(fileService.update(userToken, fileId, fileName, fileNumber, reworkHint, adjudicateType));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation(value="条件查询文档",notes =
            "其中NONE表示查询除 DELETE DRAFT 以外的所有类型，其他类型一一对应  \n" +
            "DELETE文档被删除时候的审核状态是保留的")
    public ResponseData<PageData<FileDto>> getFileList(@RequestParam("operator_token") String operatorToken,
                                                       @RequestParam(value = "file_name", required = false) String fileName,
                                                       @RequestParam(value = "file_number", required = false) String fileNumber,
                                                       @RequestParam(value = "owner_name", required = false) String ownerName,
                                                       @RequestParam(value = "owner_number", required = false) String ownerNumber,
                                                       @RequestParam(value = "department", required = false) String department,
                                                       @RequestParam(value = "start_time", required = false, defaultValue = "0") long startTime,
                                                       @RequestParam(value = "end_time", required = false, defaultValue = "0") long endTime,
                                                       @RequestParam(value = "adjudicate_type", required = false, defaultValue = "NONE") AdjudicateTypes adjudicateType,
                                                       @RequestParam("start_index") int startIndex,
                                                       @RequestParam("end_index") int endIndex) {

        Pageable pageRequest = PageOffsetRequest.getPageableByIndex(
                startIndex,
                endIndex,
                new Sort(Sort.Direction.DESC, "utime"));
        PageData<FileDto> pageData = fileService.list(
                operatorToken,
                fileName,
                fileNumber,
                ownerName,
                ownerNumber,
                department,
                startTime,
                endTime,
                adjudicateType,
                pageRequest);
        return ResponseData.ok(pageData);
    }

    @Autowired
    FileHolder fileHolder;

    @RequestMapping(value = "/upload_document", method = RequestMethod.POST)
    public ResponseData<FileDto> upload(@RequestParam("operator_token") String operatorToken,
                                        @RequestParam("file_id") String fileId,
                                        @RequestParam MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileLink = fileHolder.saveFile(file);
        return ResponseData.ok(fileService.uploadDocument(fileId, fileName, fileLink));
    }


    @RequestMapping(value = "/get_document", method = RequestMethod.GET)
    @ApiOperation("附件文档下载")
    public ResponseEntity<InputStreamResource> getDocument(@RequestParam("operator_token") String operatorToken,
                                                           @RequestParam("url") String url) {
        return fileHolder.getFileResponse(url);
    }

    @RequestMapping(value = "/delete_document", method = RequestMethod.DELETE)
    @ApiOperation("删除某文档的附件(返回删除后的文档信息)")
    public ResponseData<FileDto> deleteDocument(@RequestParam("operator_token") String operatorToken,
                                                @RequestParam("document_id") String documentId) {
        return ResponseData.ok(fileService.deleteDocument(documentId));
    }

    @RequestMapping(value = "/delete_document_multiple", method = RequestMethod.DELETE)
    @ApiOperation("删除多个附件文档(无返回)")
    public ResponseData<String> deleteDocument(@RequestParam("operator_token") String operatorToken,
                                               @RequestParam("document_id_List") List<String> documentIdList) {
        fileService.deleteDocumentMultiple(documentIdList);
        return ResponseData.ok("OK");
    }


    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiOperation("删除文档")
    public ResponseData<String> delete(@RequestParam("operator_token") String operatorToken,
                                       @RequestParam("file_id") String fileId) {
        fileService.deleteFile(fileId);
        return ResponseData.ok("OK");
    }

    @RequestMapping(value = "/delete_multiple", method = RequestMethod.DELETE)
    @ApiOperation("删除多个文档")
    public ResponseData<String> deleteMultiple(@RequestParam("operator_token") String operatorToken,
                                               @RequestParam("file_id_list") List<String> fileIdList) {
        fileService.deleteFileMultiple(fileIdList);
        return ResponseData.ok("OK");
    }

    @RequestMapping(value = "/recover", method = RequestMethod.POST)
    @ApiOperation("恢复文档")
    public ResponseData<String> recover(@RequestParam("operator_token") String operatorToken,
                                        @RequestParam("file_id") String fileId) {
        fileService.recoverFile(fileId);
        return ResponseData.ok("OK");
    }

    @RequestMapping(value = "/recover_multiple", method = RequestMethod.POST)
    @ApiOperation("恢复多个文档")
    public ResponseData<String> recoverMultiple(@RequestParam("operator_token") String operatorToken,
                                                @RequestParam("file_id_list") List<String> fileIdList) {
        fileService.recoverFileMultiple(fileIdList);
        return ResponseData.ok("OK");
    }

    @RequestMapping(value = "/get_file_by_id", method = RequestMethod.GET)
    public ResponseData<FileDto> getFileById(@RequestParam("operator_token") String operatorToken,
                                             @RequestParam("id") String id) {
        return ResponseData.ok(fileService.getFileDtoById(id));
    }
}
