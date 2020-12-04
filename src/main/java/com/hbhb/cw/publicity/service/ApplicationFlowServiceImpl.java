package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.mapper.ApplicationFlowMapper;
import com.hbhb.cw.publicity.model.ApplicationFlow;
import com.hbhb.cw.publicity.web.vo.ApplicationFlowNodeVO;

import java.util.List;

import javax.annotation.Resource;

/**
 * @author yzc
 * @since 2020-12-03
 */
public class ApplicationFlowServiceImpl implements ApplicationFlowService {

    @Resource
    private ApplicationFlowMapper applicationFlowMapper;

    @Override
    public void deleteByBatchNum(String batchNum) {
        applicationFlowMapper.deleteByBatchNum(batchNum);
    }

    @Override
    public void insertBatch(List<ApplicationFlow> list) {
        applicationFlowMapper.insertBatch(list);
    }

    @Override
    public ApplicationFlow getInfoById(Long id) {
       return applicationFlowMapper.selectById(id);
    }

    @Override
    public void updateBatchByNodeId(List<ApplicationFlowNodeVO> approvers, String batchNum) {
        // 更新各节点审批人
        applicationFlowMapper.updateBatchByNodeId(approvers, batchNum);
    }
}
