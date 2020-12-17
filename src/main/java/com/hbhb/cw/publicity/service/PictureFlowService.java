package com.hbhb.cw.publicity.service;

import com.hbhb.cw.flowcenter.vo.FlowApproveInfoVO;
import com.hbhb.cw.publicity.model.PictureFlow;
import com.hbhb.cw.publicity.web.vo.PictureApproveVO;

import java.util.List;

/**
 * @author wangxiaogang
 */
public interface PictureFlowService {
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
    void deletePictureFlow(Long printId);

    /**
     * 批量新增流程信息
     *
     * @param pictureFlowList 列表
     */
    void insertBatch(List<PictureFlow> pictureFlowList);

    /**
     * 查看印刷用品列表
     *
     * @param pictureId 印刷品id
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
    void approve(PictureApproveVO vo, Integer userId);

    /**
     * 删除流程
     *
     * @param id id
     */
    void deletePrintFlow(Long id);
}
