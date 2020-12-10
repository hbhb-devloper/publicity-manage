package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.Application;
import com.hbhb.cw.publicity.web.vo.ApplicationByUnitVO;
import com.hbhb.cw.publicity.web.vo.GoodsChangerVO;
import com.hbhb.cw.publicity.web.vo.GoodsCondVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;

import java.util.List;

/**
 * @author yzc
 * @since 2020-11-23
 */
public interface ApplicationMapper extends BaseMapper<Application> {
    List<Application> selectByCond(GoodsReqVO goodsReqVO);

    Application selectByHall(GoodsCondVO goodsCondVO);

    void updateEditable(List<Long> list);

    void updateBatch(List<GoodsChangerVO> list);

    List<Application> selectApplicationByUnitId(Integer unitId, String time, Integer goodsIndex);

    List<Application> selectByBatchNum(String batchNum);

    List<ApplicationByUnitVO> selectByUnit(GoodsReqVO goodsReqVO);
}
