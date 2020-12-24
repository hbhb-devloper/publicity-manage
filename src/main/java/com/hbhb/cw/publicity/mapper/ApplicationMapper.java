package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.Application;
import com.hbhb.cw.publicity.web.vo.ApplicationByUnitVO;

import java.util.List;

/**
 * @author yzc
 * @since 2020-11-23
 */
public interface ApplicationMapper extends BaseMapper<Application> {

    Application selectByHall(Integer unitId, String batchNum, Long hallId);

    List<Application> selectApplicationByUnitId(Integer unitId, Long hallId, String batchNum);

    List<Application> selectByBatchNum(String batchNum);

    List<ApplicationByUnitVO> selectByUnit(Integer unitId,String batchNum);
}
