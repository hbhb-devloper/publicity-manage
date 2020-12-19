package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.flowcenter.vo.FlowApproveVO;
import com.hbhb.cw.publicity.model.PictureFlow;
import org.beetl.sql.mapper.annotation.Param;
import org.beetl.sql.mapper.annotation.Update;

import java.util.List;

/**
 * @author wangxiaogang
 */
public interface PictureFlowMapper extends BaseMapper<PictureFlow> {

    /**
     * 批量更新流程节点审批人
     *
     * @param approvers 审批人
     * @param pictureId 画面id
     */
    @Update
    void updateBatchByNodeId(@Param("approver") List<FlowApproveVO> approvers, @Param("pictureId") Long pictureId);
}
