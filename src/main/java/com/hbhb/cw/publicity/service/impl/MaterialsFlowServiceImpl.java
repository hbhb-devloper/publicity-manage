package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.bean.BeanConverter;
import com.hbhb.cw.flowcenter.vo.*;
import com.hbhb.cw.publicity.enums.*;
import com.hbhb.cw.publicity.exception.PublicityException;
import com.hbhb.cw.publicity.mapper.MaterialsFlowMapper;
import com.hbhb.cw.publicity.model.MaterialsFlow;
import com.hbhb.cw.publicity.model.MaterialsNotice;
import com.hbhb.cw.publicity.rpc.*;
import com.hbhb.cw.publicity.service.MailService;
import com.hbhb.cw.publicity.service.MaterialsFlowService;
import com.hbhb.cw.publicity.service.MaterialsNoticeService;
import com.hbhb.cw.publicity.service.MaterialsService;
import com.hbhb.cw.publicity.web.vo.FlowNodeOperationVO;
import com.hbhb.cw.publicity.web.vo.MaterialsApproveVO;
import com.hbhb.cw.publicity.web.vo.MaterialsFlowVO;
import com.hbhb.cw.publicity.web.vo.MaterialsInfoVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wangxiaogang
 */
@Slf4j
@Service
public class MaterialsFlowServiceImpl implements MaterialsFlowService {
    @Resource
    private MaterialsService materialsService;
    @Resource
    private MaterialsFlowMapper flowMapper;
    @Resource
    private FlowNoticeApiExp noticeApi;
    @Resource
    private FlowTypeApiExp typeApi;
    @Resource
    private FlowRoleUserApiExp roleUserApi;
    @Resource
    private FlowApiExp flowApi;
    @Resource
    private SysUserApiExp userApi;
    @Value("${mail.enable}")
    private Boolean mailEnable;
    @Resource
    private MailService mailService;
    @Resource
    private MaterialsNoticeService noticeService;


    @Override
    public void insertBatch(List<MaterialsFlow> pictureFlowList) {

    }

    @Override
    public List<FlowApproveInfoVO> getInvoiceNodeList(Long materialsId, Integer userId) {
        // 登录用户id
        List<FlowApproveInfoVO> list = new ArrayList<>();
        // 查询流程的所有节点
        List<MaterialsFlow> flowList = flowMapper.createLambdaQuery().andEq(MaterialsFlow::getMaterialsId, materialsId).select();
        List<MaterialsFlowVO> flowNodes = BeanConverter.copyBeanList(flowList, MaterialsFlowVO.class);
        // 通过节点id得到流程类型名称
        String flowName = flowApi.getNameByNodeId(flowNodes.get(0).getFlowNodeId());
        // 通过签报id得到发票单位名称
        MaterialsInfoVO materials = materialsService.getMaterials(materialsId);
        // 签报流程名称 todo
        String materialsFlowName = materials.getUnitId() + flowName;
        // 通过userId得到nickName
        UserInfo userInfo = userApi.getUserInfoById(userId);
        // 如果流程已结束，参与流程的角色登入
        for (int i = 0; i < flowNodes.size(); i++) {
            if (flowNodes.get(i).getOperation() == null
                    || !flowNodes.get(flowNodes.size() - 1).getOperation().equals(OperationState.UN_EXECUTED.value())) {
                for (MaterialsFlowVO flowNode : flowNodes) {
                    FlowApproveInfoVO result = new FlowApproveInfoVO();
                    BeanConverter.copyProp(flowNode, result);
                    result.setApproverRole(flowNode.getRoleDesc());
                    result.setApprover(FlowApproverVO.builder()
                            .value(flowNode.getApprover()).readOnly(true).build());
                    result.setOperation(FlowOperationVO.builder()
                            .value(flowNode.getOperation()).hidden(true).build());
                    result.setSuggestion(FlowSuggestionVO.builder()
                            .value(flowNode.getSuggestion()).readOnly(true).build());
                    result.setApproverSelect(getApproverSelectList(flowNode.getFlowNodeId(),
                            materials.getId()));
                    result.setProjectFlowName(materialsFlowName);
                    list.add(result);
                }
                return list;
            }
        }
        Map<String, MaterialsFlowVO> flowNodeMap = flowNodes.stream().collect(
                Collectors.toMap(MaterialsFlowVO::getFlowNodeId, Function.identity()));
        // 通过userId得到该用户的所有流程角色
        List<Long> flowRoleIds = roleUserApi.getRoleIdByUserId(userId);
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
        // 当前节点id
        String currentNodeId = getCurrentNode(voList);
        if (!StringUtils.isEmpty(currentNodeId)) {
            MaterialsFlowVO currentNode = flowNodeMap.get(currentNodeId);
            // 2.判断登录用户是否为该审批人
            if (!userId.equals(currentNode.getApprover())) {
                // 2-1.如果不是，则所有节点信息全部为只读
                flowNodes.forEach(flowNode -> list.add(buildFlowNode(flowNode, currentNodeId, 0, materialsFlowName)));
            } else {
                // 2-2-a.如果是审批人，且为分配者
                if (flowRoleIds.contains(currentNode.getAssigner())) {
                    flowNodes.forEach(
                            flowNode -> list.add(buildFlowNode(flowNode, currentNodeId, 2, materialsFlowName)));
                } else {
                    // 2-2-b.如果是审批人，但不是分配者
                    flowNodes.forEach(
                            flowNode -> list.add(buildFlowNode(flowNode, currentNodeId, 1, materialsFlowName)));
                }

            }
        }
        // 如果是审批人，但是默认用户下拉框没有该用户的时候，加上该用户
        for (FlowApproveInfoVO fundInvoiceFlowInfoVO : list) {
            if (userId.equals(fundInvoiceFlowInfoVO.getApprover().getValue())) {
                ArrayList<Integer> userIds = new ArrayList<>();
                List<FlowRoleResVO> approverSelect = fundInvoiceFlowInfoVO.getApproverSelect();
                for (FlowRoleResVO flowRoleResVO : approverSelect) {
                    userIds.add(flowRoleResVO.getUserId());
                }
                if (!userIds.contains(userId)) {
                    FlowRoleResVO flowRoleResVO = new FlowRoleResVO();
                    flowRoleResVO.setUserId(userId);
                    flowRoleResVO.setNickName(userInfo.getNickName());
                    approverSelect.add(flowRoleResVO);
                    fundInvoiceFlowInfoVO.setApproverSelect(approverSelect);
                }
            }
        }
        // 审批未结束，具有节点审批人权限的人登入
        for (FlowApproveInfoVO infoVO : list) {
            if (!infoVO.getFlowNodeId().equals(currentNodeId)
                    && !infoVO.getOperation().getValue().equals(OperationState.UN_EXECUTED.value())) {
                infoVO.setApprover(FlowApproverVO.builder()
                        .value(infoVO.getApprover().getValue()).readOnly(true).build());
                infoVO.setOperation(FlowOperationVO.builder()
                        .value(infoVO.getOperation().getValue()).hidden(true).build());
                infoVO.setSuggestion(FlowSuggestionVO.builder()
                        .value(infoVO.getSuggestion().getValue()).readOnly(true).build());
                infoVO.setApproverSelect(getApproverSelectList(infoVO.getFlowNodeId(),
                        infoVO.getBusinessId()));
                infoVO.setProjectFlowName(materialsFlowName);
            } else {
                break;
            }
        }
        return list;
    }

    /**
     * 查询审批人下拉框值
     */
    private List<FlowRoleResVO> getApproverSelectList(String flowNodeId, Long materialsId) {
        List<MaterialsFlow> select = flowMapper.createLambdaQuery()
                .andEq(MaterialsFlow::getFlowNodeId, flowNodeId)
                .andEq(MaterialsFlow::getMaterialsId, materialsId)
                .select();
        return BeanConverter.copyBeanList(select, FlowRoleResVO.class);
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
    private FlowApproveInfoVO buildFlowNode(MaterialsFlowVO flowNode, String currentNodeId,
                                            Integer type, String projectFlowName) {
        FlowApproveInfoVO result = new FlowApproveInfoVO();
        BeanConverter.copyProp(flowNode, result);
        // 判断是否为当前节点
        boolean isCurrentNode = currentNodeId.equals(flowNode.getFlowNodeId());
        boolean approverReadOnly;
        boolean operationHidden;
        boolean suggestionReadOnly;
        boolean inputHidden;
        switch (type) {
            // 审批节点
            case 1:
                approverReadOnly = true;
                operationHidden = !isCurrentNode;
                suggestionReadOnly = !isCurrentNode;
                inputHidden = !isCurrentNode;
                break;
            // 审批节点（分配者）
            case 2:
                approverReadOnly = isCurrentNode;
                operationHidden = !isCurrentNode;
                suggestionReadOnly = !isCurrentNode;
                inputHidden = !isCurrentNode;
                break;
            // 默认只读
            default:
                approverReadOnly = true;
                operationHidden = true;
                suggestionReadOnly = true;
                inputHidden = true;

        }
        result.setApprover(FlowApproverVO.builder()
                .value(flowNode.getApprover()).readOnly(approverReadOnly).build());
        result.setOperation(FlowOperationVO.builder()
                .value(flowNode.getOperation()).hidden(operationHidden).build());
        result.setSuggestion(FlowSuggestionVO.builder()
                .value(flowNode.getSuggestion()).readOnly(suggestionReadOnly).build());
        result.setApproverSelect(getApproverSelectList(flowNode.getFlowNodeId(), flowNode.getMaterialsId()));
        result.setProjectFlowName(projectFlowName);
        result.setApproverRole(flowNode.getRoleDesc());
        result.setInput(inputHidden);
        return result;
    }


    @Override
    public void approve(MaterialsApproveVO approveVO, Integer userId) {
        // 查询当前节点信息
        MaterialsFlow currentFlow = flowMapper.single(approveVO.getId());
        // 校验审批人是否为本人
        if (!currentFlow.getUserId().equals(userId)) {
            throw new PublicityException(PublicityErrorCode.LOCK_OF_APPROVAL_ROLE);
        }

        // 预开发票id
        Long pictureId = currentFlow.getMaterialsId();
        // 当前节点id
        String currentFlowNodeId = currentFlow.getFlowNodeId();
        // 所有审批人
        List<FlowApproveVO> approvers = approveVO.getApprovers();
        // 所有节点id
        List<String> flowNodes = approvers.stream().map(
                FlowApproveVO::getFlowNodeId).collect(Collectors.toList());
        // 节点所对应的用户id
        // flowNodeId => userId
        Map<String, Integer> approverMap = new HashMap<>();
        for (FlowApproveVO approver : approvers) {
            approverMap.put(approver.getFlowNodeId(), approver.getUserId());
        }
        Integer operation = null;
        Integer projectState = null;
        // 同意
        if (approveVO.getOperation().equals(OperationState.AGREE.value())) {
            operation = OperationState.AGREE.value();
            // 1、判断是否为最后一个节点，如果是，则更新项目流程状态

            if (isLastNode(currentFlowNodeId, flowNodes)) {
                projectState = NodeState.APPROVED.value();

            } else {
                // 获取用户的所有流程角色
                List<Long> flowRoleIds = roleUserApi.getRoleIdByUserId(userId);
                // 判断当前用户是否为分配者。如果是分配者，则判断是否已指定所有审批人
                if (flowRoleIds.contains(currentFlow.getAssigner())) {
                    // 判断是否所有审批人已指定
                    if (!isAllApproverAssigned(approvers)) {
                        throw new PublicityException(PublicityErrorCode.NOT_ALL_APPROVERS_ASSIGNED);
                    }
                    // 更新各节点审批人
                    //           flowMapper.createLambdaQuery().andEq(approvers, printId).update();
                }
                // 如果不是分配者，则判断是否已指定下一个审批人
                else {
                    // 校验下一个节点的审批人是否已指定
                    Integer nextApprover = approverMap
                            .get(getNextNode(currentFlowNodeId, flowNodes));
                    if (nextApprover == null) {
                        throw new PublicityException(PublicityErrorCode.NOT_ALL_APPROVERS_ASSIGNED);
                    }
                }
            }
        }
        // 拒绝
        else if (approveVO.getOperation().equals(OperationState.REJECT.value())) {
            operation = OperationState.REJECT.value();
            projectState = NodeState.APPROVE_REJECTED.value();
        }

        // 同意或者拒绝后对该发票的代办提醒进行删除
        noticeService.updateNoticeState(pictureId);
        // 推送提醒
        assert operation != null;
        toInform(operation, approvers, userId, pictureId, currentFlowNodeId, flowNodes,
                approverMap, approveVO.getSuggestion());

        // 更新项目节点信息
        flowMapper.updateById(MaterialsFlow.builder()
                .operation(operation)
                .suggestion(approveVO.getSuggestion())
                .updateTime(new Date())
                .id(approveVO.getId())
                .build());

        // 更新项目的流程状态
        if (projectState != null) {
            materialsService.updateState(pictureId, projectState);
        }
    }

    @Override
    public void deleteMaterialsFlow(Long id) {

    }

    /**
     * 判断当前节点是否为流程中的最后一个节点
     */
    private boolean isLastNode(String currentFlowNodeId, List<String> flowNodeIds) {
        if (CollectionUtils.isEmpty(flowNodeIds)) {
            return false;
        }
        return currentFlowNodeId.equals(flowNodeIds.get(flowNodeIds.size() - 1));
    }

    /**
     * 判断是否所有节点的审批人已指定
     */
    private boolean isAllApproverAssigned(List<FlowApproveVO> approvers) {
        for (FlowApproveVO approver : approvers) {
            if (approver.getUserId() == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取当前节点的下一个节点
     */
    private String getNextNode(String currentFlowNodeId, List<String> flowNodeIds) {
        if (!flowNodeIds.contains(currentFlowNodeId) || isLastNode(currentFlowNodeId,
                flowNodeIds)) {
            return null;
        }
        return flowNodeIds.get(flowNodeIds.indexOf(currentFlowNodeId) + 1);
    }


    /**
     * 推送提醒人
     *
     * @param operation         是否同意（1同意0拒绝）
     * @param approvers         所有审批人
     * @param userId            用户id
     * @param materialsId       物料id
     * @param currentFlowNodeId 当前的节点id
     * @param flowNodes         所有的节点id
     * @param approverMap       flowNodeId => userId
     * @param suggestion        意见
     */
    private void toInform(Integer operation, List<FlowApproveVO> approvers,
                          Integer userId, Long materialsId, String currentFlowNodeId, List<String> flowNodes,
                          Map<String, Integer> approverMap, String suggestion) {
        // 通过flowNodeId得到流程类型id
        Long flowTypeId = typeApi.getTypeIdByNode(flowNodes.get(0));
        MaterialsInfoVO materials = materialsService.getMaterials(materialsId);
        String printName = materials.getMaterialsName();
        // 流程名称
        String flowName = flowApi.getNameByNodeId(flowNodes.get(0));
        // 获取用户姓名
        UserInfo user = userApi.getUserInfoById(userId);
        // 提醒信息(同意)
        if (operation.equals(OperationState.AGREE.value())) {
            // 判断是否为最后一位
            String inform;
            // 不是最后一位（提醒发起人和下一位节点）
            if (!approvers.get(approvers.size() - 1).getUserId().equals(userId)) {
                inform = getInform(currentFlowNodeId, FlowNodeNoticeState.DEFAULT_REMINDER.value());
                if (inform == null) {
                    return;
                }
                inform = inform.replace(TemplateContent.TITLE.getValue(), printName + "_" + flowName);
                // 推送下一位审批者
                Integer nextApprover = approverMap.get(getNextNode(currentFlowNodeId, flowNodes));
                noticeService.addMaterialsNotice(
                        MaterialsNotice.builder().materialsId(materialsId)
                                .receiver(nextApprover)
                                .promoter(userId)
                                .content(inform)
                                .flowTypeId(flowTypeId)
                                .build());
                // 推送邮件
                if (mailEnable) {
                    // 下一节点审批人信息
                    UserInfo userInfo = userApi.getUserInfoById(nextApprover);
                    // 推送内容
                    String info = "_" + flowName;
                    mailService.postMail(userInfo.getEmail(), userInfo.getNickName(), info);
                }
            }


            inform = getInform(currentFlowNodeId,
                    FlowNodeNoticeState.COMPLETE_REMINDER.value());
            if (inform == null) {
                return;
            }
            inform = inform.replace(TemplateContent.TITLE.getValue(), "_" + flowName);
            inform = inform.replace(TemplateContent.APPROVE.getValue(), user.getNickName());
            // 推送发起人
            noticeService.addMaterialsNotice(
                    MaterialsNotice.builder().materialsId(materialsId)
                            .receiver(approvers.get(0).getUserId())
                            .promoter(userId)
                            .content(inform)
                            .flowTypeId(flowTypeId)
                            .build());
        }
        // 拒绝
        else {
            String inform = getInform(currentFlowNodeId,
                    FlowNodeNoticeState.REJECT_REMINDER.value());
            if (inform == null) {
                return;
            }
            String replace = inform.replace(TemplateContent.TITLE.getValue(), "_" + flowName);
            inform = replace.replace(TemplateContent.APPROVE.getValue(), user.getNickName());
            inform = inform.replace(TemplateContent.CAUSE.getValue(), suggestion);
            noticeService.addMaterialsNotice(
                    MaterialsNotice.builder().materialsId(materialsId)
                            .receiver(approvers.get(0).getUserId())
                            .promoter(userId)
                            .content(inform)
                            .flowTypeId(flowTypeId)
                            .build());
        }
    }

    /**
     * 提醒信息
     */
    @Override
    public String getInform(String flowNodeId, Integer state) {
        return noticeApi.getInform(flowNodeId, state);
    }
}
