package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.flowcenter.vo.FlowApproveVO;
import com.hbhb.cw.publicity.model.PrintFlow;
import org.beetl.sql.mapper.annotation.Param;
import org.beetl.sql.mapper.annotation.Update;

import java.util.List;

/**
 * @author wangxiaogang
 */
public interface PrintFlowMapper extends BaseMapper<PrintFlow> {
    /**
     * 批量更新流程节点审批人
     *
     * @param approvers 审批人
     * @param printId   印刷品id
     */
    @Update
    void updateBatchByNodeId(@Param("approver") List<FlowApproveVO> approvers, @Param("printId") Long printId);
}
