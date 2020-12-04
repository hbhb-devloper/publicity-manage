package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.service.ApplicationService;
import com.hbhb.cw.publicity.web.vo.ApplicationApproveVO;
import com.hbhb.cw.publicity.web.vo.GoodsApproveVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsVO;
import com.hbhb.cw.publicity.web.vo.UnitGoodsStateVO;
import com.hbhb.web.annotation.UserId;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
 * @since 2020-12-01
 */
@Tag(name = "宣传管理-物料申请")
@RestController
@RequestMapping("/goods/application")
@Slf4j
public class ApplicationController {

    @Resource
    private ApplicationService applicationService;

    @GetMapping("/unit/goods")
    @Operation(summary = "营业厅物料分公司汇总（政企/市场部）")
    public SummaryUnitGoodsResVO getUnitGoods(GoodsReqVO goodsReqVO, Integer state) {
        return applicationService.getUnitGoodsList(goodsReqVO);
    }

    @GetMapping("/unit/simplex")
    @Operation(summary = "营业厅物料业务单式分公司汇总（政企/市场部）")
    public List<SummaryUnitGoodsVO> getUnitSimplexGoods(GoodsReqVO goodsReqVO) {
        return applicationService.getUnitSimplexList(goodsReqVO);
    }

    @GetMapping("/unit/single")
    @Operation(summary = "营业厅物料宣传单页分公司汇总（政企/市场部）")
    public List<SummaryUnitGoodsVO> getUnitSingleGoods( GoodsReqVO goodsReqVO) {
        return applicationService.getUnitSingleList(goodsReqVO);
    }

    @GetMapping("/unit/state")
    @Operation(summary = "营业厅物料宣传单页分公司汇总状态（政企/市场部）")
    public List<UnitGoodsStateVO> getUnitGoodsState(GoodsReqVO goodsReqVO) {
        return applicationService.getUnitGoodsStateList(goodsReqVO);
    }

    @Operation(summary = "发起审批")
    @PostMapping("/to-approve")
    public void toApprover(@RequestBody GoodsApproveVO goodsApproveVO, @Parameter(hidden = true) @UserId Integer userId){
        applicationService.toApprover(goodsApproveVO,userId);
    }

    @Operation(summary = "审批")
    @PostMapping("/approve")
    public void approve(@RequestBody ApplicationApproveVO approveVO,
                        @UserId Integer userId) {
        applicationService.approve(approveVO, userId);
    }
}
