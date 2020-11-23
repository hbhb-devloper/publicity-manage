package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.web.vo.PrintInfoVO;
import com.hbhb.cw.publicity.web.vo.PrintReqVO;
import com.hbhb.cw.publicity.web.vo.PrintResVO;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.PageResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author wangxiaogang
 */
@Tag(name = "宣传管理-印刷用品")
@RestController
@RequestMapping("/project")
@Slf4j
public class PrintController {

    @Operation(summary = "印刷用品管理列表")
    @GetMapping("/list")
    public PageResult<PrintResVO> getPrintList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "查询参数") PrintReqVO reqVO, @UserId Integer userId) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        return null;
    }

    @Operation(summary = "添加印刷品")
    @PostMapping("/add")
    public void addPrint(@Parameter(description = "新增参数实体") PrintInfoVO infoVO) {
    }

    @Operation(summary = "删除印刷品")
    @DeleteMapping("/{id}")
    public void deletePrint(@PathVariable Long id) {

    }

    @Operation(summary = "下载业务单式模板")
    @PostMapping("/export/business")
    public void exportBusiness() {

    }


    @Operation(summary = "下载宣传单页模板")
    @PostMapping("/export/publicity")
    public void exportPublicity() {

    }

    @Operation(summary = "导入")
    @PostMapping("/import")
    public void printImport() {

    }
}
