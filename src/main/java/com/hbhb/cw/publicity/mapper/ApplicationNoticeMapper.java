package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.ApplicationNotice;

/**
 * @author yzc
 * @since 2020-12-04
 */
public interface ApplicationNoticeMapper extends BaseMapper<ApplicationNotice> {
    void updateByBatchNum(String batchNum);
}
