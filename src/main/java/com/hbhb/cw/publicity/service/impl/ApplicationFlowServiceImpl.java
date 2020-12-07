package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.flowcenter.vo.FlowRoleResVO;
import com.hbhb.cw.publicity.mapper.ApplicationFlowMapper;
import com.hbhb.cw.publicity.model.ApplicationFlow;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.service.ApplicationFlowService;
import com.hbhb.cw.publicity.web.vo.ApplicationFlowInfoVO;
import com.hbhb.cw.publicity.web.vo.ApplicationFlowNodeVO;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

/**
 * @author yzc
 * @since 2020-12-03
 */
public class ApplicationFlowServiceImpl implements ApplicationFlowService {

    @Resource
    private ApplicationFlowMapper applicationFlowMapper;
    @Resource
    private SysUserApiExp sysUserApiExp;

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

    @Override
    public void updateById(ApplicationFlow applicationFlow) {
        applicationFlowMapper.updateById(applicationFlow);
    }

    @Override
    public List<ApplicationFlowInfoVO> getInfoByBatchNum(String batchNum, Integer userId) {
        List<ApplicationFlowInfoVO> list = new ArrayList<>();
//        String flowName = null;
//        // 查询流程的所有节点
//        List<ApplicationFlowVO> flowNodes = applicationFlowMapper.selectByBatch(batchNum);
//        // 通过userId得到nickName
//        UserInfo userInfo = sysUserApiExp.getUserInfoById(userId);
//        // 如果流程已结束，参与流程的角色登入
//        for (int i = 0; i < flowNodes.size(); i++) {
//            if (flowNodes.get(i).getOperation() == null
//                    || !flowNodes.get(flowNodes.size() - 1).getOperation().equals(OperationState.UN_EXECUTED.value())) {
//                for (ApplicationFlowVO flowNode : flowNodes) {
//                    ApplicationFlowInfoVO result = new ApplicationFlowInfoVO();
//                    BeanConverter.copyProp(flowNode, result);
//                    result.setApproverRole(flowNode.getRoleDesc());
//                    result.setApprover(FlowApproverVO.builder()
//                            .value(flowNode.getApprover()).readOnly(true).build());
//                    result.setOperation(FlowOperationVO.builder()
//                            .value(flowNode.getOperation()).hidden(true).build());
//                    result.setSuggestion(FlowSuggestionVO.builder()
//                            .value(flowNode.getSuggestion()).readOnly(true).build());
//                    result.setApproverSelect(getApproverSelectList(flowNode.getFlowNodeId(),
//                           batchNum));
//                    list.add(result);
//                }
//                return list;
//            }
//        }
//        Map<String, ApplicationFlowVO> flowNodeMap = flowNodes.stream().collect(
//                Collectors.toMap(ApplicationFlowVO::getFlowNodeId, Function.identity()));
//        // 通过userId得到该用户的所有流程角色
//        List<Long> flowRoleIds = flowRoleUserService.getFlowRoleIdByUserId(userId);
//        // 1.先获取流程流转的当前节点<currentNode>
//        // 2.再判断<loginUser>是否为<currentNode>的审批人
//        //   2-1.如果不是，则所有节点信息全部为只读
//        //   2-2.如果是，则判断是否为该流程的分配者
//        //      a.如果不是分配者，则只能编辑当前节点的按钮操作<operation>和意见<suggestion>
//        //      b.如果是分配者，则可以编辑以下：
//        //        当前节点的按钮操作<operation>和意见<suggestion>
//        //        其他节点的审批人<approver>
//
//        // 1.先获取流程流转的当前节点
//        List<FlowNodeOperationVO> voList = new ArrayList<>();
//        flowNodes.forEach(flowNode -> voList.add(FlowNodeOperationVO.builder()
//                .flowNodeId(flowNode.getFlowNodeId())
//                .operation(flowNode.getOperation())
//                .build()));
//        // 当前节点id
//        String currentNodeId = getCurrentNode(voList);
//        if (!StringUtils.isEmpty(currentNodeId)) {
//            BudgetProjectFlowVO currentNode = flowNodeMap.get(currentNodeId);
//            // 2.判断登录用户是否为该审批人
//            if (!userId.equals(currentNode.getApprover())) {
//                // 2-1.如果不是，则所有节点信息全部为只读
//                flowNodes.forEach(flowNode -> list.add(buildFlowNode(flowNode, currentNodeId, 0, projectFlowName)));
//            } else {
//                // 2-2-a.如果是审批人，且为分配者
//                if (flowRoleIds.contains(currentNode.getAssigner())) {
//                    flowNodes.forEach(
//                            flowNode -> list.add(buildFlowNode(flowNode, currentNodeId, 2, projectFlowName)));
//                } else {
//                    // 2-2-b.如果是审批人，但不是分配者
//                    flowNodes.forEach(
//                            flowNode -> list.add(buildFlowNode(flowNode, currentNodeId, 1, projectFlowName)));
//                }
//            }
//        }
//        // 如果是审批人，但是默认用户下拉框没有该用户的时候，加上该用户
//        for (BudgetProjectFlowInfoVO budgetProjectFlowInfoVO : list) {
//            if (userId.equals(budgetProjectFlowInfoVO.getApprover().getValue())) {
//                ArrayList<Integer> userIds = new ArrayList<>();
//                List<FlowRoleResVO> approverSelect = budgetProjectFlowInfoVO.getApproverSelect();
//                for (FlowRoleResVO flowRoleResVO : approverSelect) {
//                    userIds.add(flowRoleResVO.getUserId());
//                }
//                if (!userIds.contains(userId)) {
//                    FlowRoleResVO flowRoleResVO = new FlowRoleResVO();
//                    flowRoleResVO.setUserId(userId);
//                    flowRoleResVO.setNickName(userInfo.getNickName());
//                    approverSelect.add(flowRoleResVO);
//                    budgetProjectFlowInfoVO.setApproverSelect(approverSelect);
//                }
//            }
//        }
//        // 审批未结束，具有节点审批人权限的人登入
//        for (BudgetProjectFlowInfoVO infoVO : list) {
//            if (!infoVO.getFlowNodeId().equals(currentNodeId)
//                    && !infoVO.getOperation().getValue().equals(OperationType.UN_EXECUTED.value())) {
//                infoVO.setApprover(BudgetProjectFlowApproverVO.builder()
//                        .value(infoVO.getApprover().getValue()).readOnly(true).build());
//                infoVO.setOperation(BudgetProjectFlowOperationVO.builder()
//                        .value(infoVO.getOperation().getValue()).hidden(true).build());
//                infoVO.setSuggestion(BudgetProjectFlowSuggestionVO.builder()
//                        .value(infoVO.getSuggestion().getValue()).readOnly(true).build());
//                infoVO.setApproverSelect(getApproverSelectList(infoVO.getFlowNodeId(),
//                        Math.toIntExact(infoVO.getProjectId())));
//                infoVO.setProjectFlowName(projectFlowName);
//            } else {
//                break;
//            }
//        }
        return list;
    }

    /**
     * 查询审批人下拉框值
     */
    private List<FlowRoleResVO> getApproverSelectList(String flowNodeId, String batchNum) {
        return applicationFlowMapper
                .selectNodeByNodeId(flowNodeId, batchNum);
    }
}
