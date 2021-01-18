
package com.hbhb.cw.publicity.web.controller;

import com.alibaba.excel.EasyExcel;
import com.hbhb.api.core.bean.FileVO;
import com.hbhb.api.core.bean.SelectVO;
import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.publicity.enums.PublicityErrorCode;
import com.hbhb.cw.publicity.exception.PublicityException;
import com.hbhb.cw.publicity.rpc.FileApiExp;
import com.hbhb.cw.publicity.service.PrintService;
import com.hbhb.cw.publicity.service.listener.PrintListener;
import com.hbhb.cw.publicity.web.vo.PrintImportVO;
import com.hbhb.cw.publicity.web.vo.PrintInfoVO;
import com.hbhb.cw.publicity.web.vo.PrintInitVO;
import com.hbhb.cw.publicity.web.vo.PrintMaterialsVO;
import com.hbhb.cw.publicity.web.vo.PrintReqVO;
import com.hbhb.cw.publicity.web.vo.PrintResVO;
import com.hbhb.cw.systemcenter.enums.FileType;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageResult;
import org.beetl.sql.core.page.PageResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.alibaba.excel.util.StringUtils.isEmpty;


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
            @Parameter(description = "查询参数") PrintReqVO reqVO) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        if (reqVO.getUnitId() == null) {
            return new DefaultPageResult<>();
        }
        return printService.getPrintList(reqVO, pageNum, pageSize);
    }

    @Operation(summary = "添加印刷品")
    @PostMapping("")
    public void addPrint(@Parameter(description = "新增参数实体") @RequestBody PrintInfoVO infoVO,
                         @Parameter(hidden = true) @UserId Integer userId) {
        printService.addPrint(infoVO, userId);
    }

    @Operation(summary = "删除印刷品")
    @DeleteMapping("/{id}")
    public void deletePrint(@PathVariable Long id, @Parameter(hidden = true) @UserId Integer userId) {
        printService.deletePrint(id, userId);
    }

    @Operation(summary = "修改印刷品")
    @PutMapping("")
    public void updatePrint(@Parameter(description = "新增参数实体") @RequestBody PrintInfoVO infoVO,
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
    public String printImport(@RequestPart(required = false, value = "file") MultipartFile file,
                              @Parameter(description = "类型") Integer type) {

        String fileName = file.getOriginalFilename();
        printService.judgeFileName(fileName);
        if (isEmpty(type)) {
            throw new PublicityException(PublicityErrorCode.IMPORT_DATA_TYPE_ERROR);
        }
        AtomicInteger types = new AtomicInteger();
        types.set(type);
        long begin = System.currentTimeMillis();
        try {
            EasyExcel.read(file.getInputStream(), PrintImportVO.class,
                    new PrintListener(printService, types)).sheet().doRead();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new PublicityException(PublicityErrorCode.INPUT_DATA_ERROR);
        }
        log.info("导入成功，总共耗时：" + (System.currentTimeMillis() - begin) / 1000 + "s");
        return printService.getImportDataId();
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

        printService.toApprove(initVO, userId);
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

    @Operation(summary = "获取excel导入数据")
    @GetMapping("/materials")
    List<PrintMaterialsVO> getMaterialsList(@Parameter(description = "id") String uuid) {
        return printService.getPrintMaterialsList(uuid);
    }

    @Operation(summary = "删除导入数据")
    @DeleteMapping("/materials/{printId}")
    public void deleteMaterials(@PathVariable("printId") Long printId) {
        printService.deletePrintMaterials(printId);
    }

    @Operation(summary = "获取市场部审核员下拉列表")
    @GetMapping("/role-user")
    List<SelectVO> getAssessorList() {
        return printService.getAssessor();
    }

}

