
package com.hbhb.cw.publicity.web.controller;

import com.hbhb.api.core.bean.FileVO;
import com.hbhb.cw.publicity.rpc.FileApiExp;
import com.hbhb.cw.publicity.service.PictureService;
import com.hbhb.cw.publicity.web.vo.PictureInfoVO;
import com.hbhb.cw.publicity.web.vo.PictureInitVO;
import com.hbhb.cw.publicity.web.vo.PictureReqVO;
import com.hbhb.cw.publicity.web.vo.PictureResVO;
import com.hbhb.cw.systemcenter.enums.FileType;
import com.hbhb.web.annotation.UserId;
import com.hbhb.web.util.FileUtil;

import org.beetl.sql.core.page.PageResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangxiaogang
 */

@Tag(name = "宣传管理-宣传画面设计")
@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {
    @Resource
    private FileApiExp fileApi;
    @Resource
    private PictureService pictureService;

    @Operation(summary = "印刷用品管理列表")
    @GetMapping("/list")
    public PageResult<PictureResVO> getPictureList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "查询参数") PictureReqVO reqVO,
            @Parameter(hidden = false) @UserId Integer userId) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        return pictureService.getPictureList(reqVO, pageNum, pageSize);
    }

    @Operation(summary = "添加宣传画面")
    @PostMapping("")
    public void addPicture(@Parameter(description = "实体") @RequestBody PictureInfoVO pictureInfoVO,
                           @Parameter(hidden = true) @UserId Integer userId) {
        pictureService.addPicture(pictureInfoVO, userId);
    }

    @Operation(summary = "查看详情")
    @GetMapping("/{id}")
    public PictureInfoVO getPictureInfo(@PathVariable Long id) {
        return pictureService.getPicture(id);
    }

    @Operation(summary = "宣传画面模板下载")
    @PostMapping("/export")
    public void exportPicture(HttpServletResponse response) {
        String path = fileApi.getTemplatePath() + "/宣传画面设计需求单模板v2.doc";
        FileUtil.download(response, path, false);
    }

    @Operation(summary = "修改宣传画面")
    @PutMapping("")
    public void updatePicture(@RequestBody PictureInfoVO pictureInfoVO, @Parameter(hidden = true) @UserId Integer userId) {
        pictureService.updatePicture(pictureInfoVO, userId);
    }

    @Operation(summary = "宣传画面模板导入")
    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public FileVO pictureImport(@RequestPart(required = false, value = "file") MultipartFile file) {
        return fileApi.upload(file, 64);

    }

    @Operation(summary = "上传附件")
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public FileVO uploadPictureFile(@RequestPart(required = false, value = "file") MultipartFile file) {

        return fileApi.upload(file, FileType.PUBLICITY_PICTURE_FILE.value());
    }

    @Operation(summary = "删除宣传画面")
    @DeleteMapping("/{id}")
    public void deletePicture(@PathVariable("id") Long id) {
        pictureService.deletePicture(id);
    }

    @Operation(summary = "发起审批")
    @PostMapping("/to-approve")
    public void toApprove(@RequestBody PictureInitVO initVO, @Parameter(hidden = true) @UserId Integer userId) {
        pictureService.toApprove(initVO, userId);
    }

    @Operation(summary = "删除附件")
    @DeleteMapping("/file/{id}")
    public void deleteFile(@PathVariable Long id) {
        pictureService.deleteFile(id);
    }
}

