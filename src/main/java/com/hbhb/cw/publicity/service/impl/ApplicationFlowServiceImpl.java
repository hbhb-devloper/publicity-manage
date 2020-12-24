package com.hbhb.cw.publicity.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.hbhb.api.core.bean.SelectVO;
import com.hbhb.core.bean.BeanConverter;
import com.hbhb.cw.flowcenter.vo.NodeApproverVO;
import com.hbhb.cw.flowcenter.vo.NodeOperationVO;
import com.hbhb.cw.flowcenter.vo.NodeSuggestionVO;
import com.hbhb.cw.publicity.enums.OperationState;
import com.hbhb.cw.publicity.mapper.ApplicationFlowMapper;
import com.hbhb.cw.publicity.model.ApplicationFlow;
import com.hbhb.cw.publicity.rpc.FlowRoleUserApiExp;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.service.ApplicationFlowService;
import com.hbhb.cw.publicity.web.vo.ApplicationFlowInfoVO;
import com.hbhb.cw.publicity.web.vo.ApplicationFlowNodeVO;
import com.hbhb.cw.publicity.web.vo.ApplicationFlowVO;
import com.hbhb.cw.publicity.web.vo.FlowNodeOperationVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

/**
 * @author yzc
 * @since 2020-12-03
 */
@Service
public class ApplicationFlowServiceImpl implements ApplicationFlowService {

    @Resource
    private ApplicationFlowMapper applicationFlowMapper;
    @Resource
    private SysUserApiExp sysUserApiExp;
    @Resource
    private FlowRoleUserApiExp flowRoleUserApiExp;

    @Override
    public void deleteByBatchNum(String batchNum) {
        applicationFlowMapper.createLambdaQuery()
                .andEq(ApplicationFlow::getBatchNum,batchNum)
                .delete();
    }

    @Override
    public void insertBatch(List<ApplicationFlow> list) {
        applicationFlowMapper.insertBatch(list);
    }

    @Override
    public ApplicationFlow getInfoById(Long id) {
       return applicationFlowMapper.single(id);
    }

    @Override
    public void updateBatchByNodeId(List<ApplicationFlowNodeVO> approvers, String batchNum) {
        List<ApplicationFlow> list = new ArrayList<>();
        for (ApplicationFlowNodeVO approver : approvers) {
            list.add(ApplicationFlow.builder()
                    .batchNum(batchNum)
                    .flowNodeId(approver.getFlowNodeId())
                    .userId(approver.getUserId())
                    .id(approver.getId())
                    .build());
        }
        // 更新各节点审批人
        applicationFlowMapper.updateBatchTempById(list);
    }

    @Override
    public void updateById(ApplicationFlow applicationFlow) {
        applicationFlowMapper.updateTemplateById(applicationFlow);
    }

    @Override
    public List<ApplicationFlowInfoVO> getInfoByBatchNum(String batchNum, Integer userId) {
        List<ApplicationFlowInfoVO> list = new ArrayList<>();
        // 查询流程的所有节点
        List<ApplicationFlow> applicationFlows= applicationFlowMapper.createLambdaQuery()
                .andEq(ApplicationFlow::getBatchNum, batchNum).select();
        List<ApplicationFlowVO> flowNodes = BeanConverter.copyBeanList(applicationFlows, ApplicationFlowVO.class);
        for (ApplicationFlowVO flowNode : flowNodes) {
            flowNode.setNickName(sysUserApiExp.getUserInfoById(flowNode.getUserId()).getNickName());
        }
        // 通过userId得到nickName
        UserInfo userInfo = sysUserApiExp.getUserInfoById(userId);
        // 如果流程已结束，参与流程的角色登入
        for (int i = 0; i < flowNodes.size(); i++) {
            if (flowNodes.get(i).getOperation() == null
                    || !flowNodes.get(flowNodes.size() - 1).getOperation().equals(OperationState.UN_EXECUTED.value())) {
                for (ApplicationFlowVO flowNode : flowNodes) {
                    ApplicationFlowInfoVO result = new ApplicationFlowInfoVO();
                    BeanConverter.copyProp(flowNode, result);
                    result.setApproverRole(flowNode.getRoleDesc());
                    result.setApprover(NodeApproverVO.builder()
                            .value(flowNode.getUserId())
                            .readOnly(true)
                            .build());
                    result.setOperation(NodeOperationVO.builder()
                            .value(flowNode.getOperation())
                            .hidden(true)
                            .build());
                    result.setSuggestion(NodeSuggestionVO.builder()
                            .value(flowNode.getSuggestion())
                            .readOnly(true)
                            .build());
                    result.setApproverSelect(getApproverSelectList(flowNode.getFlowNodeId(),
                           batchNum));
                    list.add(result);
                }
                return list;
            }
        }
        Map<String, ApplicationFlowVO> flowNodeMap = flowNodes.stream().collect(
                Collectors.toMap(ApplicationFlowVO::getFlowNodeId, Function.identity()));
        // 通过userId得到该用户的所有流程角色
        List<Long> flowRoleIds = flowRoleUserApiExp.getRoleIdByUserId(userId);
        // 1.先获取流程流转的当前节点<currentNode>
        // 2.再判断<loginUser>是否为<currentNode>的审批人
        //   2-1.如果不是，则所有节点信息全部为只读
        //   2-2.如果是，则判断是否为该流程的分配者
        //      a.如果不是分配者，则只能编辑当前节点的按钮操作<operation>和意见<suggestion>
        //      b.如果是分配者，则可以编辑以下：
        //        当前节点的按钮操作<operation>和意见<suggestion>
        //        其他节点的审批人<approver>

        // 1.先获取流程流转的当前节点
        List<FlowNodeOperationVO> voList = new ArrayList<>();
        flowNodes.forEach(flowNode -> voList.add(FlowNodeOperationVO.builder()
                .flowNodeId(flowNode.getFlowNodeId())
                .operation(flowNode.getOperation())
                .build()));
        // 当前节点id
        String currentNodeId = getCurrentNode(voList);
        if (!StringUtils.isEmpty(currentNodeId)) {
            ApplicationFlowVO currentNode = flowNodeMap.get(currentNodeId);
            // 2.判断登录用户是否为该审批人
            if (!userId.equals(currentNode.getUserId())) {
                // 2-1.如果不是，则所有节点信息全部为只读
                flowNodes.forEach(flowNode -> list.add(buildFlowNode(flowNode, currentNodeId, 0)));
            } else {
                // 2-2-a.如果是审批人，且为分配者
                if (flowRoleIds.contains(currentNode.getAssigner())) {
                    flowNodes.forEach(
                            flowNode -> list.add(buildFlowNode(flowNode, currentNodeId, 2)));
                } else {
                    // 2-2-b.如果是审批人，但不是分配者
                    flowNodes.forEach(
                            flowNode -> list.add(buildFlowNode(flowNode, currentNodeId, 1)));
                }
            }
        }
        // 如果是审批人，但是默认用户下拉框没有该用户的时候，加上该用户
        for (ApplicationFlowInfoVO applicationFlowInfoVO : list) {
            if (userId.equals(applicationFlowInfoVO.getApprover().getValue())) {
                ArrayList<Integer> userIds = new ArrayList<>();
                List<SelectVO> approverSelect = applicationFlowInfoVO.getApproverSelect();
                for (SelectVO flowRoleResVO : approverSelect) {
                    userIds.add(Math.toIntExact(flowRoleResVO.getId()));
                }
                if (!userIds.contains(userId)) {
                    SelectVO flowRoleResVO = new SelectVO();
                    flowRoleResVO.setId(Long.valueOf(userId));
                    flowRoleResVO.setLabel(userInfo.getNickName());
                    approverSelect.add(flowRoleResVO);
                    applicationFlowInfoVO.setApproverSelect(approverSelect);
                }
            }
        }
        // 审批未结束，具有节点审批人权限的人登入
        for (ApplicationFlowInfoVO infoVO : list) {
            if (!infoVO.getFlowNodeId().equals(currentNodeId)
                    && !infoVO.getOperation().getValue().equals(OperationState.UN_EXECUTED.value())) {
                infoVO.setApprover(NodeApproverVO.builder()
                        .value(infoVO.getApprover().getValue()).readOnly(true).build());
                infoVO.setOperation(NodeOperationVO.builder()
                        .value(infoVO.getOperation().getValue()).hidden(true).build());
                infoVO.setSuggestion(NodeSuggestionVO.builder()
                        .value(infoVO.getSuggestion().getValue()).readOnly(true).build());
                infoVO.setApproverSelect(getApproverSelectList(infoVO.getFlowNodeId(),
                      batchNum));
            } else {
                break;
            }
        }
        return list;
    }

    /**
     * 查询审批人下拉框值
     */
    private List<SelectVO> getApproverSelectList(String flowNodeId, String batchNum) {
        Integer roleId = applicationFlowMapper
                .selectNodeByNodeId(flowNodeId,batchNum);
            // 通过角色id得到用户id
        List<Integer> userIds = flowRoleUserApiExp.getUserIdByRoleId(Long.valueOf(roleId));
        Map<Integer, String> userMap = sysUserApiExp.getUserMapById(userIds);
        return userMap.entrySet().stream().map(item ->
                SelectVO.builder()
                        .id(Long.valueOf(item.getKey()))
                        .label(item.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取流程流转的当前节点id
     *
     * @param list 已排序
     * @return 当前节点的id
     */
    private String getCurrentNode(List<FlowNodeOperationVO> list) {
        // 通过检查operation状态来确定流程流传到哪个节点
        for (FlowNodeOperationVO vo : list) {
            if (OperationState.UN_EXECUTED.value().equals(vo.getOperation())) {
                return vo.getFlowNodeId();
            }
        }
        return null;
    }

    /**
     * 组装流程节点属性
     */
    private ApplicationFlowInfoVO buildFlowNode(ApplicationFlowVO flowNode,
                                                 String currentNodeId,
                                                 Integer type) {
        ApplicationFlowInfoVO result = new ApplicationFlowInfoVO();
        BeanConverter.copyProp(flowNode, result);

        // 判断是否为当前节点
        boolean isCurrentNode = currentNodeId.equals(flowNode.getFlowNodeId());
        boolean approverReadOnly;
        boolean operationHidden;
        boolean suggestionReadOnly;
        switch (type) {
            // 审批节点
            case 1:
                approverReadOnly = true;
                operationHidden = !isCurrentNode;
                suggestionReadOnly = !isCurrentNode;
                break;
            // 审批节点（分配者）
            case 2:
                approverReadOnly = isCurrentNode;
                operationHidden = !isCurrentNode;
                suggestionReadOnly = !isCurrentNode;
                break;
            // 默认只读
            default:
                approverReadOnly = true;
                operationHidden = true;
                suggestionReadOnly = true;
        }
        result.setApprover(NodeApproverVO.builder()
                .value(flowNode.getUserId())
                .readOnly(approverReadOnly)
                .build());
        result.setOperation(NodeOperationVO.builder()
                .value(flowNode.getOperation())
                .hidden(operationHidden)
                .build());
        result.setSuggestion(NodeSuggestionVO.builder()
                .value(flowNode.getSuggestion())
                .readOnly(suggestionReadOnly)
                .build());
        result.setApproverSelect(getApproverSelectList(flowNode.getFlowNodeId(), flowNode.getBatchNum()));
        result.setApproverRole(flowNode.getRoleDesc());
        return result;
    }

}
