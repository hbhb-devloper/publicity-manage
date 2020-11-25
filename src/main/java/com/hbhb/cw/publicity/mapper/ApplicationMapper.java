package com.hbhb.cw.publicity.mapper;

import com.hbhb.cw.publicity.model.Application;
import com.hbhb.web.beetlsql.BaseMapper;

import java.util.Date;
import java.util.List;

/**
 * @author yzc
 * @since 2020-11-23
 */
public interface ApplicationMapper extends BaseMapper<Application> {
        List<Application> selectByCond(Date date, Long id);
}
