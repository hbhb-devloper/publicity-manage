package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.service.GoodsService;
import com.hbhb.cw.publicity.web.vo.VerifyGoodsVO;
import com.hbhb.cw.publicity.web.vo.VerifyHallGoodsVO;
import com.hbhb.web.annotation.UserId;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-11-25
 */
@Tag(name = "宣传管理-物料审核")
@RestController
@RequestMapping("/verify")
@Slf4j
public class VerifyGoodsController {

    @Resource
    private GoodsService goodsService;

    @GetMapping("/list")
    @Operation(summary = "审核申请列表")
    public List<VerifyGoodsVO> getList(@UserId Integer userId) {
        return goodsService.getVerifyList(userId);
    }

    @GetMapping("/hall/list")
    @Operation(summary = "审核申请列表详情")
    public List<VerifyHallGoodsVO> getUnitInfo(Integer unitId, Long goodsId) {
        return goodsService.getInfoList(unitId, goodsId);
    }

    @PutMapping("")
    @Operation(summary = "审核 通过/拒接 保存")
    public void approveGoods(Integer unitId, Long goodsId) {
       goodsService.approveUnitGoods(unitId, goodsId);
    }
}
