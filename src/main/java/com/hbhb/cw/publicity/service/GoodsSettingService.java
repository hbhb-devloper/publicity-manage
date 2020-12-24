package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.cw.publicity.web.vo.GoodsSettingResVO;
import com.hbhb.cw.publicity.web.vo.GoodsSettingVO;

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
     * @param goodsSettingVO 相关设定的list
     */
    void addGoodsSetting(GoodsSettingVO goodsSettingVO);

    /**
     * 通过时间获得次序
     */
    GoodsSettingResVO getGoodsSetting(String time);

    /**
     * 活动该次相关设置 （该次）
     */
    GoodsSetting getSetByDate(String time);

    /**
     * 通过批次改变提前截止
     */
    void updateByBatchNum(String batchNum);

    /**
     * 通过条件查找第几次
     */
    GoodsSetting getByCond(String time,Integer goodsIndex);
}
