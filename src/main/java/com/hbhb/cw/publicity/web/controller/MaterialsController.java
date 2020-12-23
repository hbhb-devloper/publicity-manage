package com.hbhb.cw.publicity.web.controller;

import com.alibaba.excel.EasyExcel;
import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.publicity.enums.PublicityErrorCode;
import com.hbhb.cw.publicity.exception.PublicityException;
import com.hbhb.cw.publicity.model.MaterialsBudget;
import com.hbhb.cw.publicity.rpc.FileApiExp;
import com.hbhb.cw.publicity.service.MaterialsService;
import com.hbhb.cw.publicity.service.listener.MaterialsListener;
import com.hbhb.cw.publicity.web.vo.*;
import com.hbhb.cw.systemcenter.enums.FileType;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wangxiaogang
 */
@Tag(name = "宣传管理-物料设计制作")
@RestController
@RequestMapping("/materials")
@Slf4j
public class MaterialsController {
    @Resource
    private FileApiExp fileApi;
    @Resource
    private MaterialsService materialsService;

    @Operation(summary = "物料设计列表")
    @GetMapping("/list")
    public PageResult<MaterialsResVO> getMaterialsList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "查询参数") MaterialsReqVO reqVO,
            @Parameter(hidden = true) @UserId Integer userId) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;

        return materialsService.getMaterialsList(reqVO, pageNum, pageSize);
    }

    @Operation(summary = "跟据id查看详情")
    @GetMapping("/{id}")
    public MaterialsInfoVO getMaterials(@PathVariable Long id) {
        return materialsService.getMaterials(id);
    }

    @Operation(summary = "添加宣传物料设计制作")
    @PostMapping("")
    private void addMaterials(@RequestBody MaterialsInfoVO materialsInfoVO,
                              @Parameter(hidden = true) @UserId Integer userId) {
        materialsService.addMaterials(materialsInfoVO, userId);
    }

    @Operation(summary = "修改宣传物料设计制作")
    @PutMapping("")
    private void updateMaterials(@RequestBody MaterialsInfoVO materialsInfoVO,
                                 @Parameter(hidden = true) @UserId Integer userId) {
        materialsService.updateMaterials(materialsInfoVO, userId);
    }

    @Operation(summary = "宣传物料导入模板下载")
    @PostMapping("/export")
    public void exportMaterials(HttpServletRequest request, HttpServletResponse response) {
        List<Object> list = new ArrayList<>();
        String fileName = ExcelUtil.encodingFileName(request, "宣传单页模板");
        ExcelUtil.export2WebWithTemplate(response, fileName, "宣传单页模板",
                fileApi.getTemplatePath() + File.separator + "宣传单页模板.xlsx", list);
    }

    @Operation(summary = "宣传画面物料导入")
    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String materialsImport(@RequestPart(required = false, value = "file") MultipartFile file, AtomicLong printId) {
        long begin = System.currentTimeMillis();

        try {
            EasyExcel.read(file.getInputStream(), PrintImportVO.class,
                    new MaterialsListener(materialsService, printId)).sheet().headRowNumber(2).doRead();
        } catch (IOException | NumberFormatException | NullPointerException e) {
            log.error(e.getMessage(), e);
            throw new PublicityException(PublicityErrorCode.INPUT_DATA_ERROR);
        }
        log.info("导入成功，总共耗时：" + (System.currentTimeMillis() - begin) / 1000 + "s");
        return materialsService.getImportDataId();
    }

    @Operation(summary = "上传附件")
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public FileVO uploadMaterialsFile(@RequestPart(required = false, value = "file") MultipartFile file) {
        return fileApi.upload(file, FileType.PUBLICITY_MATERIALS_FILE.value());
    }

    @Operation(summary = "删除宣传画面")
    @DeleteMapping("/{id}")
    public void deleteMaterials(@PathVariable("id") Long id) {
        materialsService.deleteMaterials(id);
    }

    @Operation(summary = "发起审批")
    @PostMapping("/to-approve")
    public void toApprove(@RequestBody MaterialsInitVO initVO,
                          @Parameter(hidden = true) @UserId Integer userId) {
        materialsService.toApprove(initVO);
    }

    @Operation(summary = "删除附件")
    @DeleteMapping("/file/{id}")
    public void deleteFile(@PathVariable Long id) {
        materialsService.deleteFile(id);
    }

    @Operation(summary = "修改物料制作预算控制")
    @PutMapping("/budget")
    public void updateBudget(@RequestBody List<MaterialsBudget> budget) {
        materialsService.updateBudget(budget);
    }

    @Operation(summary = "物料制作预算控制列表")
    @GetMapping("/budget/list")
    public List<MaterialsBudgetResVO> getMaterialsBudgetList() {
        return materialsService.getMaterialsBudgetList();
    }

    @Operation(summary = "物料制作控制预算统计")
    @GetMapping("/statistics")
    public MaterialsBudgetVO getMaterialsBudget(@Parameter(description = "单位") Integer unitId) {
        return materialsService.getMaterialsBudget(unitId);
    }


}
