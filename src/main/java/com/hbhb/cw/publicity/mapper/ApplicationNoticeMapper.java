package com.hbhb.cw.publicity.mapper;

import com.hbhb.cw.publicity.model.ApplicationNotice;
import com.hbhb.web.beetlsql.BaseMapper;

/**
 * @author yzc
 * @since 2020-12-04
 */
public interface ApplicationNoticeMapper extends BaseMapper<ApplicationNotice> {
    void updateByBatchNum(String batchNum);
}
