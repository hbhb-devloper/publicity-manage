package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.ApplicationDetail;
import com.hbhb.cw.publicity.web.vo.SummaryCondVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsVO;

import java.util.List;

/**
 * @author yzc
 * @since 2020-11-23
 */
public interface ApplicationDetailMapper extends BaseMapper<ApplicationDetail> {
    List<SummaryUnitGoodsVO> selectSummaryUnitByType(SummaryCondVO cond);
}
