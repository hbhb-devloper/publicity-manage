package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.service.GoodsService;
import com.hbhb.cw.publicity.web.vo.ApplicationVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.GoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsVO;
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

    @GetMapping("")
    @Operation(summary = "营业厅物料产品申请列表")
    public GoodsResVO getGoods(GoodsReqVO goodsReqVO) {
        return goodsService.getList(goodsReqVO);
    }

    @PostMapping("/apply")
    @Operation(summary = "新增申请数量")
    public void addGoods(@RequestBody List<ApplicationVO> list, @UserId Integer userId ) {
        goodsService.applyGoods(list, userId);
    }

    @GetMapping("/simplex")
    @Operation(summary = "营业厅物料分公司汇总")
    public SummaryGoodsVO getSimplexGoods(@UserId Integer userId) {
        return goodsService.getSimplexList(userId);
    }

    @GetMapping("/single")
    @Operation(summary = "营业厅物料分公司汇总")
    public SummaryGoodsVO getSingleGoods( @UserId Integer userId) {
        return goodsService.getSingleList(userId);
    }
}
