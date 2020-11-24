package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.model.GoodsSetting;

import java.util.List;

/**
 * @author yzc
 * @since 2020-11-24
 */
public interface GoodsSettingService {
    /**
     * 获取物料库的相关设定
     * @param goodsId 产品id
     * @return 相关设置
     */
    List<GoodsSetting> getList(Long goodsId);

    /**
     * 批量新增相关设定
     * @param list 相关设定的list
     */
    void addGoodsSetting(List<GoodsSetting> list);
}
