package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.service.GoodsService;
import com.hbhb.cw.publicity.web.vo.ApplicationVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.GoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsVO;
import com.hbhb.cw.publicity.web.vo.UserGoodsVO;
import com.hbhb.web.annotation.UserId;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-11-23
 */
@Tag(name = "宣传管理-物料申请")
@RestController
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    @GetMapping("/goods")
    @Operation(summary = "营业厅物料产品申请列表")
    public GoodsResVO getGoods(GoodsReqVO goodsReqVO) {
        return goodsService.getList(goodsReqVO);
    }

    @PostMapping("/apply")
    @Operation(summary = "新增申请数量")
    public void addGoods(@RequestBody List<ApplicationVO> list, @RequestBody GoodsReqVO goodsReqVO ) {
        goodsService.applyGoods(list, goodsReqVO);
    }

    @GetMapping("")
    @Operation(summary = "物料导航条信息")
    public UserGoodsVO getUserGoods(@UserId Integer userId) {
        return goodsService.getUserGoods(userId);
    }

    @GetMapping("time")
    @Operation(summary = "通过时间得到第几月的第几次")
    public Integer getUserGoods(String time) {
        return goodsService.getGoodsSetting(time);
    }

    @GetMapping("/simplex")
    @Operation(summary = "营业厅物料业务单式分公司汇总")
    public SummaryGoodsVO getSimplexGoods(GoodsReqVO goodsReqVO) {
        return goodsService.getSimplexList(goodsReqVO);
    }

    @GetMapping("/single")
    @Operation(summary = "营业厅物料宣传打野分公司汇总")
    public SummaryGoodsVO getSingleGoods( GoodsReqVO goodsReqVO) {
        return goodsService.getSingleList(goodsReqVO);
    }

    @GetMapping("/audit/simplex")
    @Operation(summary = "营业厅物料业务单式分公司汇总(审核)")
    public SummaryGoodsVO getAuditSimplexGoods(GoodsReqVO goodsReqVO, Integer state) {
        return goodsService.getAuditSimplexList(goodsReqVO, state);
    }

    @GetMapping("/audit/single")
    @Operation(summary = "营业厅物料宣传打野分公司汇总(审核)")
    public SummaryGoodsVO getAuditSingleGoods( GoodsReqVO goodsReqVO, Integer state) {
        return goodsService.getAuditSingleList(goodsReqVO, state);
    }

    @GetMapping("/unit/simplex")
    @Operation(summary = "营业厅物料业务单式分公司汇总")
    public SummaryGoodsVO getUnitSimplexGoods(GoodsReqVO goodsReqVO, Integer state) {
        return goodsService.getUnitSimplexList(goodsReqVO, state);
    }

    @GetMapping("/unit/single")
    @Operation(summary = "营业厅物料宣传打野分公司汇总")
    public SummaryGoodsVO getUnitSingleGoods( GoodsReqVO goodsReqVO, Integer state) {
        return goodsService.getUnitSingleList(goodsReqVO, state);
    }
}
