package com.hbhb.cw.publicity.mapper;

import com.hbhb.cw.publicity.model.Application;
import com.hbhb.cw.publicity.model.Goods;
import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.GoodsVO;
import com.hbhb.web.beetlsql.BaseMapper;

import java.util.Date;
import java.util.List;

/**
 * @author wangxiaogang
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVO> selectByCond(GoodsReqVO cond);

    GoodsSetting selectSetByDate(String time);

    List<Application> selectApplicationByUnitId(Integer unitId, Date time);
}
