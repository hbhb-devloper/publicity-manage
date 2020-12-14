package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.Application;
import com.hbhb.cw.publicity.web.vo.ApplicationByUnitVO;
import com.hbhb.cw.publicity.web.vo.GoodsChangerVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;

import java.util.List;

/**
 * @author yzc
 * @since 2020-11-23
 */
public interface ApplicationMapper extends BaseMapper<Application> {
    List<Application> selectByCond(Integer unitId, String batchNum);

    Application selectByHall(Integer unitId, String batchNum, Long hallId);

    void updateBatch(List<GoodsChangerVO> list);

    List<Application> selectApplicationByUnitId(Integer unitId, String batchNum);

    List<Application> selectByBatchNum(String batchNum);

    List<ApplicationByUnitVO> selectByUnit(GoodsReqVO goodsReqVO);
}
