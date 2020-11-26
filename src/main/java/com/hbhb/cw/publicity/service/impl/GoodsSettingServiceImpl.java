package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.publicity.mapper.GoodsSettingMapper;
import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.cw.publicity.service.GoodsSettingService;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

/**
 * @author yzc
 * @since 2020-11-24
 */
public class GoodsSettingServiceImpl implements GoodsSettingService {

    @Resource
    private GoodsSettingMapper goodsSettingMapper;

    @Override
    public List<GoodsSetting> getList(Long goodsId) {
        // 得到当前月份
        String time = DateUtil.formatDate(new Date(), "yyyy-MM");
        // 通过月份和产品id得到该产品当月的设置
        return goodsSettingMapper.selectByCond(time, goodsId);
    }

    @Override
    public void addGoodsSetting(List<GoodsSetting> list) {
        // 批量新增物料库相关设定
        goodsSettingMapper.insertBatch(list);
    }
}
