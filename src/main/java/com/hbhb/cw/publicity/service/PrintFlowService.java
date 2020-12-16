package com.hbhb.cw.publicity.service;

import com.hbhb.cw.flowcenter.vo.FlowApproveInfoVO;
import com.hbhb.cw.publicity.model.PrintFlow;
import com.hbhb.cw.publicity.web.vo.PrintApproveVO;

import java.util.List;

/**
 * @author wangxiaogang
 */
public interface PrintFlowService {
    /**
     * 获取提醒
     *
     * @param flowNodeId 节点id
     * @param value      默认提醒
     * @return 提醒信息
     */
    String getInform(String flowNodeId, Integer value);

    /**
     * 跟据印刷用品id删除对应流程
     *
     * @param printId id
     */
    void deletePrintFlow(Long printId);

    /**
     * 批量新增流程信息
     *
     * @param printFlowList 列表
     */
    void insertBatch(List<PrintFlow> printFlowList);

    /**
     * 查看印刷用品列表
     *
     * @param printId 印刷品id
     * @param userId  用户id
     * @return 流程列表
     */
    List<FlowApproveInfoVO> getInvoiceNodeList(Long printId, Integer userId);

    /**
     * 审批发票
     *
     * @param vo     条件
     * @param userId id
     */
    void approve(PrintApproveVO vo, Integer userId);
}
