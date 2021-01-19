package com.hbhb.cw.publicity.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.hbhb.api.core.bean.SelectVO;
import com.hbhb.core.bean.BeanConverter;
import com.hbhb.cw.flowcenter.enums.FlowOperationType;
import com.hbhb.cw.flowcenter.vo.NodeApproverVO;
import com.hbhb.cw.flowcenter.vo.NodeOperationReqVO;
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
import com.hbhb.cw.publicity.web.vo.FlowWrapperApplicationVO;
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
    public FlowWrapperApplicationVO getInfoByBatchNum(String batchNum, Integer userId, Integer unitId) {
        FlowWrapperApplicationVO wrapper = new FlowWrapperApplicationVO();
        List<ApplicationFlowInfoVO> list = new ArrayList<>();
        // 通过userId得到nickName
        UserInfo userInfo = sysUserApiExp.getUserInfoById(userId);
        // 查询流程的所有节点
        List<ApplicationFlow> applicationFlows= applicationFlowMapper.createLambdaQuery()
                .andEq(ApplicationFlow::getBatchNum, batchNum)
                .andEq(ApplicationFlow::getUnitId,unitId)
                .select();
        if (applicationFlows==null||applicationFlows.size()==0){
            return wrapper;
        }
        List<ApplicationFlowVO> flowNodes = BeanConverter.copyBeanList(applicationFlows, ApplicationFlowVO.class);
        for (ApplicationFlowVO flowNode : flowNodes) {
            flowNode.setNickName(sysUserApiExp.getUserInfoById(flowNode.getUserId()).getNickName());
        }
        Map<String, ApplicationFlowVO> flowNodeMap = flowNodes.stream().collect(
                Collectors.toMap(ApplicationFlowVO::getFlowNodeId, Function.identity()));
        // 判断流程是否已结束
        // 根据最后一个节点的状态可判断整个流程的状态
        if (flowNodes.get(flowNodes.size() - 1).getOperation().equals(FlowOperationType.UN_EXECUTED.value())) {
            // 1.先获取流程流转的当前节点<currentNode>
            // 2.再判断<loginUser>是否为<currentNode>的审批人
            //   2-1.如果不是，则所有节点信息全部为只读
            //   2-2.如果是，则判断是否为该流程的分配者
            //      a.如果不是分配者，则只能编辑当前节点的按钮操作<operation>和意见<suggestion>
            //      b.如果是分配者，则可以编辑以下：
            //        当前节点的按钮操作<operation>和意见<suggestion>
            //        其他节点的审批人<approver>

            // 1.先获取流程流转的当前节点
            List<NodeOperationReqVO> voList = new ArrayList<>();
            flowNodes.forEach(flowNode -> voList.add(NodeOperationReqVO.builder()
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
                    flowNodes.forEach(flowNode -> list.add(buildFlowNode(userInfo.getUnitId(),flowNode, currentNodeId, 0)));
                } else {
                    // 通过userId得到该用户的所有流程角色
                    List<Long> flowRoleIds = flowRoleUserApiExp.getRoleIdByUserId(userId);
                    // 2-2-a.如果是审批人，且为分配者
                    if (flowRoleIds.contains(currentNode.getAssigner())) {
                        flowNodes.forEach(
                                flowNode -> list.add(buildFlowNode(userInfo.getUnitId(),flowNode, currentNodeId, 2)));
                    } else {
                        // 2-2-b.如果是审批人，但不是分配者
                        flowNodes.forEach(
                                flowNode -> list.add(buildFlowNode(userInfo.getUnitId(),flowNode, currentNodeId, 1)));
                    }
                }
            }
            // 当前节点序号
            wrapper.setIndex(getCurrentNodeIndex(voList));
        }  // 如果流程已结束，则所有节点只读，不能操作
        else {
            flowNodes.forEach(flowNode -> list.add(buildFlowNode(userInfo.getUnitId(),flowNode, "", 0)));
            // 当前节点序号
            wrapper.setIndex(0);
        }
        // 如果是审批人，但是默认用户下拉框没有该用户的时候，加上该用户
        for (ApplicationFlowInfoVO vo : list) {
            // 如果当前用户是审批人，而该用户已被解除该流程角色时，加上该用户姓名
            if (userId.equals(vo.getApprover().getValue())) {
                List<Long> userIds = vo.getApproverSelect()
                        .stream().map(SelectVO::getId).collect(Collectors.toList());
                if (!userIds.contains(Long.valueOf(userId))) {
                    vo.getApproverSelect().add(SelectVO.builder()
                            .id(Long.valueOf(userId))
                            .label(userInfo.getNickName())
                            .build());
                }
            }
        }
        wrapper.setNodes(list);
        return wrapper;
    }

    /**
     * 查询审批人下拉框值
     */
    private List<SelectVO> getApproverSelectList(Integer unitId,String flowNodeId, String batchNum) {
        Integer roleId = applicationFlowMapper
                .selectNodeByNodeId(flowNodeId,batchNum);
        return flowRoleUserApiExp.getUserByRoleAndUnit(unitId, Long.valueOf(roleId));
    }

    /**
     * 获取流程流转的当前节点id
     *
     * @param list 已排序
     * @return 当前节点的id
     */
    private String getCurrentNode(List<NodeOperationReqVO>  list) {
        // 通过检查operation状态来确定流程流传到哪个节点
        for (NodeOperationReqVO vo : list) {
            if (OperationState.UN_EXECUTED.value().equals(vo.getOperation())) {
                return vo.getFlowNodeId();
            }
        }
        return null;
    }

    /**
     * 获取流程流转的当前节点序号
     */
    private Integer getCurrentNodeIndex(List<NodeOperationReqVO> list) {
        for (int i = 0; i < list.size(); i++) {
            if (FlowOperationType.UN_EXECUTED.value().equals(list.get(i).getOperation())) {
                return i;
            }
        }
        return null;
    }

    /**
     * 组装流程节点属性
     */
    private ApplicationFlowInfoVO buildFlowNode( Integer unitId,
                                                 ApplicationFlowVO flowNode,
                                                 String currentNodeId,
                                                 Integer type) {
        ApplicationFlowInfoVO result = new ApplicationFlowInfoVO();
        BeanConverter.copyProp(flowNode, result);

        // 判断是否为当前节点
        boolean isCurrentNode = currentNodeId.equals(flowNode.getFlowNodeId());
        boolean approverReadOnly;
        boolean operationHidden;
        boolean suggestionReadOnly;
        // 可编辑字段
        List<String> filedList = new ArrayList<>();
        // 是否请求下拉框的数据
        boolean requestSelectData = true;
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
                requestSelectData = false;
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
        result.setApproveTime(flowNode.getUpdateTime());
        // 如果节点已经操作过，则不返回下拉框列表；如果节点未操作，则返回
        if (requestSelectData && flowNode.getOperation().equals(FlowOperationType.UN_EXECUTED.value())) {
            result.setApproverSelect(getApproverSelectList(unitId,flowNode.getFlowNodeId(), flowNode.getBatchNum()));
        } else {
            result.setApproverSelect(new ArrayList<>());
        }
        result.setRoleDesc(flowNode.getRoleDesc());
        result.setFiledList(filedList);
        return result;
    }

}
