package com.hbhb.cw.publicity.mapper;

import com.hbhb.cw.publicity.model.Application;
import com.hbhb.cw.publicity.web.vo.GoodsChangerVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.web.beetlsql.BaseMapper;

import java.util.List;

/**
 * @author yzc
 * @since 2020-11-23
 */
public interface ApplicationMapper extends BaseMapper<Application> {
    List<Application> selectByCond(GoodsReqVO goodsReqVO);

    void updateEditable(List<Long> list);

    void updateSubmit(List<Long> list);

    void updateBatch(List<GoodsChangerVO> list);

    List<Application> selectApplicationByUnitId(Integer unitId, String time, Integer goodsIndex);
}
