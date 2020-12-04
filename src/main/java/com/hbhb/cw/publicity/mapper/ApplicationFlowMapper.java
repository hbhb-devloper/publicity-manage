package com.hbhb.cw.publicity.mapper;

import com.hbhb.cw.publicity.model.ApplicationFlow;
import com.hbhb.cw.publicity.web.vo.ApplicationFlowNodeVO;
import com.hbhb.web.beetlsql.BaseMapper;

import java.util.List;

/**
 * @author wangxiaogang
 */
public interface ApplicationFlowMapper extends BaseMapper<ApplicationFlow> {
    void deleteByBatchNum(String batchNum);

    ApplicationFlow selectById(Long id);

    void updateBatchByNodeId(List<ApplicationFlowNodeVO> approvers, String batchNum);
}
