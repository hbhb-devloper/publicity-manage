package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.model.ApplicationFlow;
import com.hbhb.cw.publicity.web.vo.ApplicationFlowNodeVO;

import java.util.List;

/**
 * @author yzc
 * @since 2020-12-03
 */
public interface ApplicationFlowService {

    /**
     * 通过batchNum（批次号）删除流程节点
     */
    void deleteByBatchNum(String batchNum);

    /**
     * 批量新增
     */
    void insertBatch(List<ApplicationFlow> list);

    /**
     * 通过节点id得到节点详情
     */
    ApplicationFlow getInfoById(Long id);


    /**
     * 更新各节点审批人
     */
    void updateBatchByNodeId( List<ApplicationFlowNodeVO> approvers, String batchNum);
}
