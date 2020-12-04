package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.model.GoodsSetting;

import java.util.List;

/**
 * @author yzc
 * @since 2020-11-24
 */
public interface GoodsSettingService {
    /**
     * 获取物料库的相关设定  （该月）
     * @return 相关设置
     */
    List<GoodsSetting> getList();

    /**
     * 批量新增相关设定
     * @param list 相关设定的list
     */
    void addGoodsSetting(List<GoodsSetting> list);

    /**
     * 活动该次相关设置 （该次）
     */
    GoodsSetting getSetByDate(String time);

    /**
     * 通过id删除设置
     */
    void deleteGoodsSetting(Long id);

    /**
     * 通过批次改变提前截止
     */
    void updateByBatchNum(String batchNum);
}
