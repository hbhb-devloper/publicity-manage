package com.hbhb.cw.publicity.web.controller;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.publicity.service.VerifyGoodsService;
import com.hbhb.cw.publicity.web.vo.GoodsChangerVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsResVO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

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
        if (goodsReqVO.getTime() == null) {
            goodsReqVO.setTime(DateUtil.dateToString(new Date()));
        }
        return verifyGoodsService.getAuditList(goodsReqVO);
    }

    @PutMapping("/save")
    @Operation(summary = "分公司保存物料")
    public void saveGoods(@RequestBody GoodsReqVO goodsReqVO) {
        verifyGoodsService.saveGoods(goodsReqVO);
    }

    @PutMapping("/submit")
    @Operation(summary = "分公司提交物料")
    public void submitGoods(@RequestBody GoodsReqVO goodsReqVO) {
        verifyGoodsService.submitGoods(goodsReqVO);
    }

    @PutMapping("/changer")
    @Operation(summary = "分公司修改修改后申请数量")
    public void changerModifyAmount(@RequestBody List<GoodsChangerVO> list) {
        verifyGoodsService.changerModifyAmount(list);
    }

    @PostMapping("/export")
    @Operation(summary = "分公司提交物料导出")
    public void summaryExport(GoodsReqVO goodsReqVO) {
        verifyGoodsService.getExportList(goodsReqVO);
    }
}
