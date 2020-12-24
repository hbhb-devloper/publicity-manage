package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.ApplicationFlow;

/**
 * @author wangxiaogang
 */
public interface ApplicationFlowMapper extends BaseMapper<ApplicationFlow> {
    Integer selectNodeByNodeId(String flowNodeId,String batchNum);
}
