package com.hbhb.cw.publicity.mapper;

import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.web.beetlsql.BaseMapper;

import java.util.List;

/**
 * @author wangxiaogang
 */
public interface GoodsSettingMapper extends BaseMapper<com.hbhb.cw.publicity.model.GoodsSetting> {
    List<GoodsSetting> selectByDate(String time);

    GoodsSetting selectSetByDate(String time);
}
