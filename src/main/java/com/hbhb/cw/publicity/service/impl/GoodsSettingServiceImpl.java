package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.publicity.mapper.GoodsSettingMapper;
import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.cw.publicity.service.GoodsSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author yzc
 * @since 2020-11-24
 */
@Service
@Slf4j
public class GoodsSettingServiceImpl implements GoodsSettingService {

    @Resource
    private GoodsSettingMapper goodsSettingMapper;

    @Override
    public List<GoodsSetting> getList() {
        // 得到当前月份
        String time = DateUtil.formatDate(new Date(), "yyyy-MM");
        // 通过月份得到该产品当月的设置
        return goodsSettingMapper.selectByDate(time);
    }

    @Override
    public void addGoodsSetting(List<GoodsSetting> list) {
        // 批量新增物料库相关设定
        goodsSettingMapper.insertBatch(list);
    }

    @Override
    public GoodsSetting getSetByDate(String time) {
        return goodsSettingMapper.selectSetByDate(time);
    }
}
