package com.hbhb.cw.publicity.service;

import com.hbhb.cw.flowcenter.vo.FlowApproveInfoVO;
import com.hbhb.cw.publicity.model.MaterialsFlow;
import com.hbhb.cw.publicity.web.vo.MaterialsApproveVO;

import java.util.List;

/**
 * @author wangxiaogang
 */
public interface MaterialsFlowService {
    /**
     * 获取提醒
     *
     * @param flowNodeId 节点id
     * @param value      默认提醒
     * @return 提醒信息
     */
    String getInform(String flowNodeId, Integer value);

    /**
     * 批量新增流程信息
     *
     * @param pictureFlowList 列表
     */
    void insertBatch(List<MaterialsFlow> pictureFlowList);

    /**
     * 查看物料制作列表
     *
     * @param pictureId 物料制作id
     * @param userId    用户id
     * @return 流程列表
     */
    List<FlowApproveInfoVO> getInvoiceNodeList(Long pictureId, Integer userId);

    /**
     * 审批发票
     *
     * @param vo     条件
     * @param userId id
     */
    void approve(MaterialsApproveVO vo, Integer userId);

    /**
     * 删除流程
     *
     * @param materialsId 物料id
     */
    void deleteMaterialsFlow(Long materialsId);
}
