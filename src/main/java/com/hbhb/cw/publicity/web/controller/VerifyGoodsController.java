package com.hbhb.cw.publicity.web.controller;

import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.publicity.service.VerifyGoodsService;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.GoodsSaveGoodsVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsResVO;
import com.hbhb.cw.publicity.web.vo.VerifyGoodsExportVO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-11-25
 */
@Tag(name = "宣传管理-分公司物料提交")
@RestController
@RequestMapping("/verify")
@Slf4j
public class VerifyGoodsController {

    @Resource
    private VerifyGoodsService verifyGoodsService;

    @GetMapping("/list")
    @Operation(summary = "营业厅物料宣传单页分公司汇总(审核)")
    public SummaryGoodsResVO getAuditGoods(GoodsReqVO goodsReqVO) {
        return verifyGoodsService.getAuditList(goodsReqVO);
    }

    @PutMapping("/save")
    @Operation(summary = "分公司保存物料")
    public void saveGoods(@RequestBody GoodsSaveGoodsVO goodsSaveGoodsVO) {
        verifyGoodsService.saveGoods(goodsSaveGoodsVO);
    }

    @PutMapping("/submit")
    @Operation(summary = "分公司提交物料")
    public void submitGoods(@RequestBody GoodsSaveGoodsVO goodsSaveGoodsVO) {
        verifyGoodsService.submitGoods(goodsSaveGoodsVO);
    }

    @PostMapping("/export")
    @Operation(summary = "分公司提交物料导出")
    public void summaryExport(HttpServletRequest request, HttpServletResponse response,@RequestBody GoodsReqVO goodsReqVO) {
        List<List<VerifyGoodsExportVO>> list = verifyGoodsService.getExportList(goodsReqVO);
        List<List> exportList = new ArrayList<>();
        for (List<VerifyGoodsExportVO> verifyGoods : list) {
            exportList.add(verifyGoods);
        }
        List<String> nameList = new ArrayList<>();
        nameList.add("业务单式");
        nameList.add("宣传单页");
        String fileName = ExcelUtil.encodingFileName(request, "分公司汇总表");
        ExcelUtil.exportManySheetWeb(response, fileName, nameList, VerifyGoodsExportVO.class, exportList);
    }
}
