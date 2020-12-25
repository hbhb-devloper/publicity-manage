package com.hbhb.cw.publicity.web.controller;

import com.hbhb.cw.publicity.api.PublicitySettingApi;
import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.cw.publicity.service.GoodsSettingService;
import com.hbhb.cw.publicity.web.vo.GoodsSettingResVO;
import com.hbhb.cw.publicity.web.vo.GoodsSettingVO;

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
@Tag(name = "宣传管理-物料库相关设定")
@RestController
@RequestMapping("/goods/setting")
@Slf4j
public class GoodsSettingController implements PublicitySettingApi {

    @Resource
    private GoodsSettingService goodsSettingService;

    @GetMapping("")
    @Operation(summary = "物料活动产品列表")
    public List<GoodsSetting> getGoodsSetting() {
        return goodsSettingService.getList();
    }

    @PostMapping("/add")
    @Operation(summary = "新增物料设定")
    public void addGoodsSetting(@RequestBody GoodsSettingVO goodsSettingVO) {
        goodsSettingService.addGoodsSetting(goodsSettingVO);
    }

    @GetMapping("/time")
    @Operation(summary = "通过时间得到第几月的有几次")
    public GoodsSettingResVO getUserGoods(String time) {
        return goodsSettingService.getGoodsSetting(time);
    }

    @Override
    public void addGoodsSetting() {
        goodsSettingService.addNextMonthSetting();
    }
}
