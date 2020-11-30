package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.web.vo.MaterialsInfoVO;
import com.hbhb.cw.publicity.web.vo.MaterialsReqVO;
import com.hbhb.cw.publicity.web.vo.MaterialsResVO;
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
@Tag(name = "宣传管理-物料设计制作")
@RestController
@RequestMapping("/materials")
@Slf4j
public class MaterialsController {
    @Operation(summary = "物料设计列表")
    @GetMapping("/list")
    public PageResult<MaterialsResVO> getMaterialsList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "查询参数") MaterialsReqVO reqVO, @UserId Integer userId) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        return null;
    }

    @Operation(summary = "跟据id查看详情")
    @GetMapping("/{id}")
    public MaterialsResVO getMaterials(@PathVariable Long id) {
        return null;
    }

    @Operation(summary = "添加宣传物料设计制作")
    @PostMapping("/add")
    private void addMaterials(@RequestBody MaterialsInfoVO materialsInfoVO) {

    }

    @Operation(summary = "宣传物料导入模板下载")
    @PostMapping("/export")
    public void exportMaterials(HttpServletRequest request, HttpServletResponse response) {

    }

    @Operation(summary = "宣传画面模板导入")
    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void materialsImport(@RequestPart(required = false, value = "file") MultipartFile file, Long printId) {

    }

    @Operation(summary = "上传附件")
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public FileVO uploadMaterialsFile(@RequestPart(required = false, value = "file") MultipartFile file) {
        return null;
    }

    @Operation(summary = "删除宣传画面")
    @DeleteMapping("/{id}")
    public void deleteMaterials(@PathVariable("id") Long id) {

    }

}
