
package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.web.vo.PictureInfoVO;
import com.hbhb.cw.publicity.web.vo.PictureResVO;
import com.hbhb.cw.systemcenter.vo.FileVO;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.PageResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author wangxiaogang
 */

@Tag(name = "宣传管理-宣传画面设计")
@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {
    @Operation(summary = "印刷用品管理列表")
    @GetMapping("/list")
    public PageResult<PictureResVO> getPictureList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "查询参数") PictureResVO reqVO, @UserId Integer userId) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        return null;
    }

    @Operation(summary = "添加宣传画面")
    @PostMapping("/add")
    public void addPicture(@RequestBody PictureInfoVO pictureInfoVO) {

    }

    @Operation(summary = "查看详情")
    @GetMapping("/{id}")
    public PictureResVO getPictureInfo(@PathVariable Long id) {
        return null;
    }

    @Operation(summary = "宣传画面模板下载")
    @PostMapping("/export")
    public void exportPicture(HttpServletRequest request, HttpServletResponse response) {

    }

    @Operation(summary = "宣传画面模板导入")
    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void pictureImport(@RequestPart(required = false, value = "file") MultipartFile file, Long printId) {

    }

    @Operation(summary = "上传附件")
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public FileVO uploadPictureFile(@RequestPart(required = false, value = "file") MultipartFile file) {
        return null;
    }

    @Operation(summary = "删除宣传画面")
    @DeleteMapping("/{id}")
    public void deletePicture(@PathVariable("id") Long id) {

    }

}

