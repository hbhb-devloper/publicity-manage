package com.hbhb.cw.publicity.service.impl;

import com.hbhb.api.core.bean.SelectVO;
import com.hbhb.core.bean.BeanConverter;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.flowcenter.enums.FlowNodeNoticeState;
import com.hbhb.cw.flowcenter.enums.FlowNodeNoticeTemp;
import com.hbhb.cw.flowcenter.enums.FlowOperationType;
import com.hbhb.cw.flowcenter.enums.FlowState;
import com.hbhb.cw.flowcenter.vo.FlowApproveVO;
import com.hbhb.cw.flowcenter.vo.FlowWrapperVO;
import com.hbhb.cw.flowcenter.vo.NodeApproverReqVO;
import com.hbhb.cw.flowcenter.vo.NodeApproverVO;
import com.hbhb.cw.flowcenter.vo.NodeInfoVO;
import com.hbhb.cw.flowcenter.vo.NodeOperationReqVO;
import com.hbhb.cw.flowcenter.vo.NodeOperationVO;
import com.hbhb.cw.flowcenter.vo.NodeSuggestionVO;
import com.hbhb.cw.publicity.enums.PublicityErrorCode;
import com.hbhb.cw.publicity.enums.Suggestion;
import com.hbhb.cw.publicity.exception.PublicityException;
import com.hbhb.cw.publicity.mapper.MaterialsFlowMapper;
import com.hbhb.cw.publicity.mapper.MaterialsMapper;
import com.hbhb.cw.publicity.mapper.MaterialsNoticeMapper;
import com.hbhb.cw.publicity.model.Materials;
import com.hbhb.cw.publicity.model.MaterialsFlow;
import com.hbhb.cw.publicity.model.MaterialsNotice;
import com.hbhb.cw.publicity.rpc.FlowApiExp;
import com.hbhb.cw.publicity.rpc.FlowNoticeApiExp;
import com.hbhb.cw.publicity.rpc.FlowRoleApiExp;
import com.hbhb.cw.publicity.rpc.FlowRoleUserApiExp;
import com.hbhb.cw.publicity.rpc.FlowTypeApiExp;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.service.MailService;
import com.hbhb.cw.publicity.service.MaterialsFlowService;
import com.hbhb.cw.publicity.web.vo.MaterialsFlowVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * @author wangxiaogang
 */
@Slf4j
@Service
class MaterialsFlowServiceImpl implements MaterialsFlowService {
    @Resource
    private MaterialsMapper materialsMapper;
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
    private MaterialsNoticeMapper noticeMapper;

    @Resource
    private FlowRoleApiExp roleApi;


    @Override
    public void approve(FlowApproveVO approveVO, Integer userId) {
        // 查询当前节点信息
        MaterialsFlow currentNode = flowMapper.single(approveVO.getId());
        // 校验审批人是否为本人
        if (!currentNode.getUserId().equals(userId)) {
            throw new PublicityException(PublicityErrorCode.LOCK_OF_APPROVAL_ROLE);
        }

        Date now = new Date();
        UserInfo userInfo = userApi.getUserInfoById(userId);
        // 当前节点id
        String currentNodeId = currentNode.getFlowNodeId();
        // 所有审批人
        List<NodeApproverReqVO> approvers = approveVO.getApprovers();
        // 审批人map（节点id <=> 审批人id）
        Map<String, Integer> approverMap = approvers.stream()
                .collect(Collectors.toMap(NodeApproverReqVO::getFlowNodeId, NodeApproverReqVO::getUserId));
        // 所有节点id
        List<String> nodeIds = approvers.stream()
                .map(NodeApproverReqVO::getFlowNodeId).collect(Collectors.toList());

        // 印刷品id
        Long materialsId = currentNode.getMaterialsId();

        Materials picture = materialsMapper.single(materialsId);
        // 流程名称
        String flowName = flowApi.getNameByNodeId(nodeIds.get(0));
        // 流程类型
        Long flowTypeId = typeApi.getTypeIdByNode(nodeIds.get(0));
        // 提醒标题 = 印刷品名称_编号_流程名称
        String title = picture.getMaterialsName() + "_" + picture.getMaterialsNum() + "_" + flowName;
        // 节点操作
        Integer operation = null;
        // 流程状态
        Integer flowState = null;

        // 更新当前节点之前的所有节点提醒状态为已读
        noticeMapper.updateTemplateById(MaterialsNotice.builder()
                .id(materialsId)
                .state(1)
                .build());

        // 同意
        if (FlowOperationType.AGREE.value().equals(approveVO.getOperation())) {
            operation = FlowOperationType.AGREE.value();
            // 1.判断当前是否为最后一个节点
            // 如果是最后一个节点，则需要更新流程状态
            if (isLastNode(currentNodeId, nodeIds)) {
                flowState = FlowState.APPROVED.value();
            }
            // 如果不是最后一个节点
            else {
                // 当前用户的所有流程角色
                List<Long> flowRoleIds = roleUserApi.getRoleIdByUserId(userId);
                // 下一个节点的审批人
                Integer next = approverMap.get(getNextNode(currentNodeId, nodeIds));
                // 2.判断当前用户是否为分配者
                // 2-1.如果是分配者
                if (flowRoleIds.contains(currentNode.getAssigner())) {
                    // 校验是否所有节点的审批人已指定
                    if (approvers.stream().anyMatch(vo -> vo.getUserId() == null)) {
                        throw new PublicityException(PublicityErrorCode.NOT_ALL_APPROVERS_ASSIGNED);
                    }
                    // 更新各节点审批人
                    flowMapper.updateBatchTempById(approvers.stream().map(vo ->
                            MaterialsFlow.builder()
                                    .id(vo.getId())
                                    .userId(vo.getUserId())
                                    .build()).collect(Collectors.toList()));
                }
                // 2-2.如果不是分配者
                else {
                    // 校验下一个节点的审批人是否已指定
                    if (next == null) {
                        throw new PublicityException(PublicityErrorCode.NOT_ALL_APPROVERS_ASSIGNED);
                    }
                }

                // 3.保存提醒消息
                // 3-1.提醒下一个节点的审批人
                String inform = noticeApi.getInform(currentNodeId, FlowNodeNoticeState.DEFAULT_REMINDER.value());
                if (!isEmpty(inform)) {
                    this.saveNotice(materialsId, next, userId,
                            inform.replace(FlowNodeNoticeTemp.TITLE.value(), title), flowTypeId, now);
                }
                // 3-2.邮件推送
                if (mailEnable) {
                    UserInfo nextUser = userApi.getUserInfoById(next);
                    mailService.postMail(nextUser.getEmail(), nextUser.getNickName(), title);
                }
            }
            // 3-3.提醒发起人
            String inform = noticeApi.getInform(currentNodeId, FlowNodeNoticeState.COMPLETE_REMINDER.value());
            if (inform != null) {


                String content = inform.replace(FlowNodeNoticeTemp.TITLE.value(), title)
                        .replace(FlowNodeNoticeTemp.APPROVE.value(), userInfo.getNickName());
                this.saveNotice(materialsId, approvers.get(0).getUserId(), userId, content, flowTypeId, now);
            }
        }
        // 拒绝
        else if (approveVO.getOperation().equals(FlowOperationType.REJECT.value())) {
            operation = FlowOperationType.REJECT.value();
            flowState = FlowState.APPROVE_REJECTED.value();
            // 提醒发起人
            String inform = noticeApi.getInform(currentNodeId, FlowNodeNoticeState.REJECT_REMINDER.value());
            if (inform == null) {
                inform = Suggestion.REFUSE.value();
            }
            String content = inform.replace(FlowNodeNoticeTemp.TITLE.value(), title)
                    .replace(FlowNodeNoticeTemp.APPROVE.value(), userInfo.getNickName())
                    .replace(FlowNodeNoticeTemp.CAUSE.value(), approveVO.getSuggestion());
            this.saveNotice(materialsId, approvers.get(0).getUserId(), userId, content, flowTypeId, now);
        }

        // 更新节点信息
        flowMapper.updateTemplateById(MaterialsFlow.builder()
                .operation(operation)
                .suggestion(approveVO.getSuggestion())
                .updateTime(now)
                .id(approveVO.getId())
                .build());

        // 更新流程状态
        if (flowState != null) {
            materialsMapper.updateTemplateById(
                    Materials.builder().id(materialsId).state(flowState).build());
        }
    }


    private void saveNotice(Long invoiceId, Integer receiver, Integer userId, String content,
                            Long flowTypeId, Date now) {
        noticeMapper.insertTemplate(MaterialsNotice.builder()
                .materialsId(invoiceId)
                .receiver(receiver)
                .promoter(userId)
                .content(content)
                .flowTypeId(flowTypeId)
                .createTime(now)
                .build());
    }

    /**
     * 判断当前节点是否为流程中的最后一个节点
     */
    private boolean isLastNode(String currentNodeId, List<String> nodeIds) {
        return currentNodeId.equals(nodeIds.get(nodeIds.size() - 1));
    }

    /**
     * 获取当前节点的下一个节点
     */
    private String getNextNode(String currentNodeId, List<String> nodeIds) {
        if (!nodeIds.contains(currentNodeId) || isLastNode(currentNodeId, nodeIds)) {
            return null;
        }
        return nodeIds.get(nodeIds.indexOf(currentNodeId) + 1);
    }


    @Override
    public FlowWrapperVO getMaterialsNodeList(Long materialsId, Integer userId) {
        FlowWrapperVO wrapper = new FlowWrapperVO();
        List<NodeInfoVO> nodes = new ArrayList<>();
        UserInfo userInfo = userApi.getUserInfoById(userId);

        // 查询流程的所有节点
        List<MaterialsFlowVO> flowNodes = this.getFlowNodes(materialsId);
        Map<String, MaterialsFlowVO> flowNodeMap = flowNodes.stream().collect(
                Collectors.toMap(MaterialsFlowVO::getFlowNodeId, Function.identity()));

        // 签报流程名称 = 印刷品画面单位名称 + 流程类型名称
        String flowName = flowApi.getNameByNodeId(flowNodes.get(0).getFlowNodeId());
        Materials materials = materialsMapper.single(materialsId);
        wrapper.setName(materials.getMaterialsName() + flowName);
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
            //      c.特殊节点

            // 1.先获取流程流转的当前节点
            List<NodeOperationReqVO> operations = new ArrayList<>();
            // 当前节点id
            flowNodes.forEach(flowNode -> {
                operations.add(NodeOperationReqVO.builder()
                        .flowNodeId(flowNode.getFlowNodeId())
                        .operation(flowNode.getOperation())
                        .build());
            });
            String currentNodeId = getCurrentNode(operations);
            if (!StringUtils.isEmpty(currentNodeId)) {
                MaterialsFlowVO currentNode = flowNodeMap.get(currentNodeId);
                // 2.判断登录用户是否为当前节点的审批人
                // 2-1.如果不是，则所有节点信息全部为只读
                if (!userId.equals(currentNode.getApprover())) {
                    flowNodes.forEach(flowNode -> nodes.add(buildFlowNode(flowNode, currentNodeId, 0)));
                }
                // 2-2.如果是，则判断是否为该流程的分配者
                else {
                    // 用户的所有流程角色
                    List<Long> flowRoleIds = roleUserApi.getRoleIdByUserId(userId);
                    // 2-2-a.当前用户是分配者
                    if (flowRoleIds.contains(currentNode.getAssigner())) {
                        flowNodes.forEach(flowNode -> nodes.add(buildFlowNode(flowNode, currentNodeId, 2)));
                    }
                    // 2-2-b.当前用户不是分配者
                    else {
                        flowNodes.forEach(flowNode -> nodes.add(buildFlowNode(flowNode, currentNodeId, 1)));
                    }

                }
            }
            // 当前节点序号
            wrapper.setIndex(getCurrentNodeIndex(operations));
        }
        // 如果流程已结束，则所有节点只读，不能操作
        else {
            flowNodes.forEach(flowNode -> nodes.add(buildFlowNode(flowNode, "", 0)));
            // 当前节点序号
            wrapper.setIndex(0);
        }
        // 解决用户被解除流程角色后，审批人下拉框显示id而非姓名的情况
        for (NodeInfoVO vo : nodes) {
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
        wrapper.setNodes(nodes);
        return wrapper;
    }

    /**
     * 获取流程的节点列表
     */
    private List<MaterialsFlowVO> getFlowNodes(Long invoiceId) {
        List<MaterialsFlow> flows = flowMapper.createLambdaQuery()
                .andEq(MaterialsFlow::getMaterialsId, invoiceId)
                .select();
        return Optional.ofNullable(flows)
                .orElse(new ArrayList<>())
                .stream()
                .map(flow -> {
                    MaterialsFlowVO vo = BeanConverter.convert(flow, MaterialsFlowVO.class);
                    // 此处节点个数有限，循环中使用rpc接口无妨
                    UserInfo userInfo = userApi.getUserInfoById(flow.getUserId());
                    vo.setMaterialsId(flow.getMaterialsId());
                    vo.setApproverRole(roleApi.getNameById(flow.getFlowRoleId()));
                    vo.setNickName(userInfo == null ? null : userInfo.getNickName());
                    vo.setApprover(flow.getUserId());
                    vo.setUpdateTime(DateUtil.formatDate(flow.getUpdateTime(), DateUtil.FORMAT_PATTERN_COMM));
                    return vo;
                }).collect(Collectors.toList());
    }

    /**
     * 获取流程流转的当前节点id
     *
     * @param list 已排序
     * @return 当前节点的id
     */
    private String getCurrentNode(List<NodeOperationReqVO> list) {
        // 通过检查operation状态来确定流程流传到哪个节点
        for (NodeOperationReqVO vo : list) {
            if (FlowOperationType.UN_EXECUTED.value().equals(vo.getOperation())) {
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
     * 组装流程节点
     */
    private NodeInfoVO buildFlowNode(MaterialsFlowVO flowNode, String currentNodeId, Integer type) {
        NodeInfoVO result = new NodeInfoVO();
        BeanConverter.copyProp(flowNode, result);
        // 判断是否为当前节点
        boolean isCurrentNode = currentNodeId.equals(flowNode.getFlowNodeId());
        // 审批人是否只读
        boolean approverReadOnly;
        // 操作按钮是否隐藏
        boolean operationHidden;
        // 意见是否只读
        boolean suggestionReadOnly;
        // 可编辑字段
        List<String> filedList = new ArrayList<>();
        // 是否请求下拉框的数据
        boolean requestSelectData = true;
        switch (type) {
            // 审批节点（非分配者）
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
                .value(flowNode.getApprover())
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
            result.setApproverSelect(getApproverSelect(flowNode.getFlowNodeId(), flowNode.getMaterialsId()));
        } else {
            result.setApproverSelect(new ArrayList<>());
        }
        result.setRoleDesc(flowNode.getRoleDesc());
        result.setFiledList(filedList);
        return result;
    }

    /**
     * 查询审批人下拉框列表
     */
    private List<SelectVO> getApproverSelect(String flowNodeId, Long businessId) {
        MaterialsFlow flow = flowMapper.createLambdaQuery()
                .andEq(MaterialsFlow::getMaterialsId, businessId)
                .andEq(MaterialsFlow::getFlowNodeId, flowNodeId)
                .single();
        // 查询该节点的流程角色所对应的用户
        List<Integer> userIds = roleUserApi.getUserIdByRoleId(flow.getFlowRoleId());
        Map<Integer, String> userMap = userApi.getUserMapById(userIds);
        return userMap.entrySet().stream().map(item ->
                SelectVO.builder()
                        .id(Long.valueOf(item.getKey()))
                        .label(item.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 提醒信息
     */
    @Override
    public String getInform(String flowNodeId, Integer state) {
        return noticeApi.getInform(flowNodeId, state);
    }


    @Override
    public void deleteMaterialsFlow(Long materialsId) {
        flowMapper.createLambdaQuery().andEq(MaterialsFlow::getMaterialsId, materialsId).delete();
    }

    @Override
    public void insertBatch(List<MaterialsFlow> materialsFlowList) {
        flowMapper.insertBatch(materialsFlowList);
    }

}
