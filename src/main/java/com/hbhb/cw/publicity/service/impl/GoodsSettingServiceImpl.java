package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.cw.publicity.service.GoodsSettingService;

import java.util.List;

/**
 * @author yzc
 * @since 2020-11-24
 */
public class GoodsSettingServiceImpl implements GoodsSettingService {
    @Override
    public List<GoodsSetting> getList(Long goodsId) {
        // 得到当前月份

        // 通过月份和产品id得到该产品当月的设置

        return null;
    }

    @Override
    public void addGoodsSetting(List<GoodsSetting> list) {
        // 批量新增物料库相关设定
    }
}
