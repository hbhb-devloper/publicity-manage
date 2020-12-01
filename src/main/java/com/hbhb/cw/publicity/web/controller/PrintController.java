
package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.service.PrintService;
import com.hbhb.cw.publicity.web.vo.PrintInfoVO;
import com.hbhb.cw.publicity.web.vo.PrintInitVO;
import com.hbhb.cw.publicity.web.vo.PrintReqVO;
import com.hbhb.cw.publicity.web.vo.PrintResVO;
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


/**
 * @author wangxiaogang
 */

@Tag(name = "宣传管理-印刷用品")
@RestController
@RequestMapping("/print")
@Slf4j
public class PrintController {

    @Resource
    private PrintService printService;

    @Operation(summary = "印刷用品管理列表")
    @GetMapping("/list")
    public PageResult<PrintResVO> getPrintList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "查询参数") PrintReqVO reqVO, @UserId Integer userId) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;

        return printService.getPrintList(reqVO, pageNum, pageSize);
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
    public void exportBusiness(HttpServletRequest request, HttpServletResponse response) {

    }


    @Operation(summary = "下载宣传单页模板")
    @PostMapping("/export/publicity")
    public void exportPublicity(HttpServletRequest request, HttpServletResponse response) {

    }

    @Operation(summary = "导入")
    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void printImport(@RequestPart(required = false, value = "file") MultipartFile file, Long printId) {

    }

    @Operation(summary = "上传附件")
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public FileVO uploadPrintFile(@RequestPart(required = false, value = "file") MultipartFile file) {
        return null;
    }

    @Operation(summary = "发起审批")
    @PostMapping("/to-approve")
    public void toApprove(@RequestBody PrintInitVO initVO, @UserId Integer userId) {

    }
}

