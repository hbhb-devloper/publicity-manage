package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.service.ApplicationDetailService;
import com.hbhb.cw.publicity.web.vo.ApplicationApproveVO;
import com.hbhb.cw.publicity.web.vo.GoodsApproveVO;
import com.hbhb.cw.publicity.web.vo.GoodsCheckerResVO;
import com.hbhb.cw.publicity.web.vo.GoodsCheckerVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.SummaryByUnitVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsVO;
import com.hbhb.cw.publicity.web.vo.UnitGoodsStateVO;
import com.hbhb.cw.publicity.web.vo.VerifyGoodsVO;
import com.hbhb.cw.publicity.web.vo.VerifyHallGoodsVO;
import com.hbhb.web.annotation.UserId;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-12-14
 */
@Tag(name = "宣传管理-物料负责分公司审核")
@RestController
@RequestMapping("/application/detail")
@Slf4j
public class ApplicationDetailController {

    @Resource
    private ApplicationDetailService applicationDetailService;

    @GetMapping("/goods")
    @Operation(summary = "营业厅物料分公司汇总（政企/市场部）（页面）")
    public SummaryUnitGoodsResVO getUnitGoods(GoodsReqVO goodsReqVO) {
        return applicationDetailService.getUnitGoodsList(goodsReqVO);
    }

    @GetMapping("/info/list")
    @Operation(summary = "营业厅物料业务单式分公司汇总（政企/市场部）（页面）")
    public SummaryByUnitVO getInfoList(GoodsReqVO goodsReqVO) {
        return applicationDetailService.getInfoList(goodsReqVO);
    }

    @GetMapping("/state")
    @Operation(summary = "营业厅物料宣传单页分公司汇总状态（政企/市场部）（页面）")
    public List<UnitGoodsStateVO> getUnitGoodsState(GoodsReqVO goodsReqVO) {
        return applicationDetailService.getUnitGoodsStateList(goodsReqVO);
    }

    @GetMapping("summary/list")
    @Operation(summary = "获取市场部或政企的汇总")
    public List<SummaryUnitGoodsVO> getSummary(GoodsReqVO goodsReqVO, Integer type){
        return applicationDetailService.getUnitSummaryList(goodsReqVO,type);
    }

    @GetMapping("/list")
    @Operation(summary = "审核申请列表（工作台）")
    public List<VerifyGoodsVO> getList(@Parameter(hidden = true) @UserId Integer userId) {
        return applicationDetailService.getList(userId);
    }

    @GetMapping("/hall/list")
    @Operation(summary = "审核申请列表详情（工作台）")
    public List<VerifyHallGoodsVO> getUnitInfo(GoodsCheckerVO goodsCheckerVO) {
        return applicationDetailService.getHallList(goodsCheckerVO);
    }

    @PutMapping("")
    @Operation(summary = "审核 通过/拒接 保存（工作台）")
    public void approveGoods(@RequestBody GoodsCheckerResVO goodsCheckerResVO) {
        applicationDetailService.approveUnitGoods(goodsCheckerResVO);
    }

    @Operation(summary = "发起审批")
    @PostMapping("/to-approve")
    public void toApprover(@RequestBody GoodsApproveVO goodsApproveVO, @Parameter(hidden = true) @UserId Integer userId){
        applicationDetailService.toApprover(goodsApproveVO,userId);
    }

    @Operation(summary = "审批")
    @PostMapping("/approve")
    public void approve(@RequestBody ApplicationApproveVO approveVO,
                        @Parameter(hidden = true) @UserId Integer userId) {
        applicationDetailService.approve(approveVO, userId);
    }
}
