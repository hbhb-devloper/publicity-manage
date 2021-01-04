package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.service.ApplicationService;
import com.hbhb.cw.publicity.web.vo.GoodsCondAppVO;
import com.hbhb.cw.publicity.web.vo.GoodsCondVO;
import com.hbhb.cw.publicity.web.vo.GoodsResVO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-12-01
 */
@Tag(name = "宣传管理-物料申请")
@RestController
@RequestMapping("/application")
@Slf4j
public class ApplicationController {

    @Resource
    private ApplicationService applicationService;

    @GetMapping("/list")
    @Operation(summary = "营业厅物料产品申请列表")
    public GoodsResVO getGoods(GoodsCondVO goodsCondVO) {
        return applicationService.getList(goodsCondVO);
    }

    @PostMapping("/apply")
    @Operation(summary = "新增申请数量")
    public void addGoods(@RequestBody GoodsCondAppVO goodsCondAppVO) {
        applicationService.applyGoods(goodsCondAppVO);
    }

}
