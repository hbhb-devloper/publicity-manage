
package com.hbhb.cw.publicity.web.controller;

import com.alibaba.excel.EasyExcel;
import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.publicity.rpc.FileApiExp;
import com.hbhb.cw.publicity.service.PrintService;
import com.hbhb.cw.publicity.service.listener.PrintListener;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


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

    @Resource
    private FileApiExp fileApi;

    @Operation(summary = "印刷用品管理列表")
    @GetMapping("/list")
    public PageResult<PrintResVO> getPrintList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            @Parameter(description = "查询参数") PrintReqVO reqVO,
            @Parameter(hidden = true) @UserId Integer userId) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;

        return printService.getPrintList(reqVO, pageNum, pageSize);
    }

    @Operation(summary = "添加印刷品")
    @PostMapping("/add")
    public void addPrint(@Parameter(description = "新增参数实体") PrintInfoVO infoVO,
                         @Parameter(hidden = true) @UserId Integer userId) {
        printService.addPrint(infoVO, userId);
    }

    @Operation(summary = "删除印刷品")
    @DeleteMapping("/{id}")
    public void deletePrint(@PathVariable Long id) {
        printService.deletePrint(id);
    }

    @Operation(summary = "修改印刷品")
    @PostMapping("/update")
    public void updatePrint(@Parameter(description = "新增参数实体") PrintInfoVO infoVO,
                            @Parameter(hidden = true) @UserId Integer userId) {
        printService.updatePrint(infoVO, userId);
    }

    @Operation(summary = "下载业务单式模板")
    @PostMapping("/export/business")
    public void exportBusiness(HttpServletRequest request, HttpServletResponse response) {
        List<Object> list = new ArrayList<>();
        String fileName = ExcelUtil.encodingFileName(request, "中国移动杭州分公司业务单式导入模板");
        ExcelUtil.export2WebWithTemplate(response, fileName, "中国移动杭州分公司业务单式导入模板",
                fileApi.getTemplatePath() + File.separator + "中国移动杭州分公司业务单式导入模板.xlsx", list);
    }

    @Operation(summary = "下载宣传单页模板")
    @PostMapping("/export/publicity")
    public void exportPublicity(HttpServletRequest request, HttpServletResponse response) {
        List<Object> list = new ArrayList<>();
        String fileName = ExcelUtil.encodingFileName(request, "中国移动杭州分公司宣传单页导入模板");
        ExcelUtil.export2WebWithTemplate(response, fileName, "中国移动杭州分公司宣传单页导入模板",
                fileApi.getTemplatePath() + File.separator + "中国移动杭州分公司宣传单页导入模板.xlsx", list);
    }

    @Operation(summary = "导入")
    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void printImport(@RequestPart(required = false, value = "file") MultipartFile file, AtomicLong printId, AtomicInteger type) {
        long begin = System.currentTimeMillis();
        try {
            EasyExcel.read(file.getInputStream(), PrintImportVO.class,
                    new PrintListener(printService, printId, type)).sheet().headRowNumber(2).doRead();
        } catch (IOException | NumberFormatException | NullPointerException e) {
            log.error(e.getMessage(), e);
        }
        log.info("导入成功，总共耗时：" + (System.currentTimeMillis() - begin) / 1000 + "s");
    }

    @Operation(summary = "上传附件")
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public FileVO uploadPrintFile(@RequestPart(required = false, value = "file") MultipartFile file) {

        return fileApi.upload(file, FileType.PUBLICITY_PRINT_FILE.value());
    }

    @Operation(summary = "发起审批")
    @PostMapping("/to-approve")
    public void toApprove(@RequestBody PrintInitVO initVO,
                          @Parameter(hidden = true) @UserId Integer userId) {
        printService.toApprove(initVO);
    }

    @Operation(summary = "删除附件")
    @DeleteMapping("/file/{id}")
    public void deleteFile(@PathVariable Long id) {
        printService.deleteFile(id);
    }

    @Operation(summary = "跟据id查询详情")
    @GetMapping("/{id}")
    public PrintInfoVO getPrint(@PathVariable Long id) {
        return printService.getPrint(id);
    }
}

