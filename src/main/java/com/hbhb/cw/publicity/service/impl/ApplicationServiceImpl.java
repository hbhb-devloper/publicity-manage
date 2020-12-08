package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.flowcenter.model.Flow;
import com.hbhb.cw.flowcenter.vo.FlowNodeNoticeResVO;
import com.hbhb.cw.flowcenter.vo.FlowNodePropVO;
import com.hbhb.cw.publicity.enums.ApplicationState;
import com.hbhb.cw.publicity.enums.FlowNodeNoticeState;
import com.hbhb.cw.publicity.enums.GoodsErrorCode;
import com.hbhb.cw.publicity.enums.GoodsType;
import com.hbhb.cw.publicity.enums.NodeState;
import com.hbhb.cw.publicity.enums.OperationState;
import com.hbhb.cw.publicity.exception.GoodsException;
import com.hbhb.cw.publicity.mapper.ApplicationMapper;
import com.hbhb.cw.publicity.model.Application;
import com.hbhb.cw.publicity.model.ApplicationFlow;
import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.cw.publicity.rpc.FlowNodeApiExp;
import com.hbhb.cw.publicity.rpc.FlowNoticeApiExp;
import com.hbhb.cw.publicity.rpc.FlowRoleUserApiExp;
import com.hbhb.cw.publicity.rpc.FlowTypeApiExp;
import com.hbhb.cw.publicity.rpc.MailApiExp;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.rpc.UnitApiExp;
import com.hbhb.cw.publicity.service.ApplicationFlowService;
import com.hbhb.cw.publicity.service.ApplicationNoticeService;
import com.hbhb.cw.publicity.service.ApplicationService;
import com.hbhb.cw.publicity.service.GoodsService;
import com.hbhb.cw.publicity.service.GoodsSettingService;
import com.hbhb.cw.publicity.web.vo.ApplicationApproveVO;
import com.hbhb.cw.publicity.web.vo.ApplicationByUnitVO;
import com.hbhb.cw.publicity.web.vo.ApplicationFlowNodeVO;
import com.hbhb.cw.publicity.web.vo.ApplicationNoticeVO;
import com.hbhb.cw.publicity.web.vo.GoodsApproveVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsVO;
import com.hbhb.cw.publicity.web.vo.UnitGoodsStateVO;
import com.hbhb.cw.systemcenter.vo.UnitTopVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

/**
 * @author yzc
 * @since 2020-12-02
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Resource
    private FlowTypeApiExp flowTypeApiExp;
    @Resource
    private FlowNodeApiExp flowNodeApiExp;
    @Resource
    private FlowRoleUserApiExp flowRoleUserApiExp;
    @Resource
    private FlowNoticeApiExp flowNoticeApiExp;
    @Resource
    private GoodsSettingService goodsSettingService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private UnitApiExp unitApiExp;
    @Resource
    private SysUserApiExp sysUserApiExp;
    @Resource
    private MailApiExp mailApiExp;
    @Resource
    private ApplicationFlowService applicationFlowService;
    @Resource
    private ApplicationNoticeService applicationNoticeService;
    @Resource
    private ApplicationMapper applicationMapper;
//    @Value("${mail.enable}")
//    private Boolean mailEnable;

    @Override
    public SummaryUnitGoodsResVO getUnitGoodsList(GoodsReqVO goodsReqVO) {
        String date = goodsReqVO.getTime();
        // 与截止时间对比，判断为第几月第几次
        GoodsSetting goodsSetting = goodsSettingService.getSetByDate(goodsReqVO.getTime());
        List<SummaryUnitGoodsVO> simSummaryList = getUnitSummaryList(goodsReqVO, GoodsType.BUSINESS_SIMPLEX.getValue());
        List<SummaryUnitGoodsVO> singSummaryList = getUnitSummaryList(goodsReqVO, GoodsType.FLYER_PAGE.getValue());
        // 通过goodsId得到unitName


        Map<String, SummaryUnitGoodsVO> map = new HashMap<>();
        for (SummaryUnitGoodsVO summaryUnitGoodsVO : simSummaryList) {
            map.put(summaryUnitGoodsVO.getGoodsId() + summaryUnitGoodsVO.getUnitName(), summaryUnitGoodsVO);
        }
        for (SummaryUnitGoodsVO cond : singSummaryList) {
            if (map.get(cond.getGoodsId() + cond.getUnitName()) == null) {
                simSummaryList.add(cond);
            } else {
                map.get(cond.getGoodsId() + cond.getUnitName()).setSingleAmount(cond.getSingleAmount());
            }
        }
        // 得到第几次，判断此次是否结束。
        if (goodsSetting.getIsEnd() != null || goodsSetting.getDeadline().getTime() < DateUtil.stringToDate(date).getTime()) {
            // 如果结束提交置灰
            return new SummaryUnitGoodsResVO(simSummaryList, false);
        }
        // 展示该次该管理部门下的申请汇总。
        return new SummaryUnitGoodsResVO(simSummaryList, true);
    }

    @Override
    public List<SummaryUnitGoodsVO> getUnitSimplexList(GoodsReqVO goodsReqVO) {
        return getUnitSummaryList(goodsReqVO, GoodsType.BUSINESS_SIMPLEX.getValue());
    }

    @Override
    public List<SummaryUnitGoodsVO> getUnitSingleList(GoodsReqVO goodsReqVO) {
        return getUnitSummaryList(goodsReqVO, GoodsType.FLYER_PAGE.getValue());
    }

    @Override
    public List<UnitGoodsStateVO> getUnitGoodsStateList(GoodsReqVO goodsReqVO) {
        // 用sql语句完成
        List<ApplicationByUnitVO> list = applicationMapper.selectByUnit(goodsReqVO);
        // 得到所有公司下所有货物（goodsId）
        HashMap<String, List<ApplicationByUnitVO>> map = new HashMap<>();
        Map<Integer, List<ApplicationByUnitVO>> collect = list.stream()
                .collect(Collectors.groupingBy(ApplicationByUnitVO::getUnitId));
        // 得到该货物的物料审核员（updateBy）和审核状态

        // 通过单位id得到该分公司下的所有单位和状态

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toApprover(GoodsApproveVO goodsApproveVO, Integer userId) {
        // 获取登入用户单位
        UserInfo user = sysUserApiExp.getUserInfoById(userId);
        //  2.获取流程id 通过流程状态活动流程id
        List<Flow> flows = flowTypeApiExp.getFlowsByTypeId(goodsApproveVO.getFlowTypeId());
        if (flows == null || flows.size() != 1) {
            throw new GoodsException(GoodsErrorCode.NOT_RELEVANT_FLOW);
        }
        Flow flow = flows.get(0);
        List<FlowNodePropVO> flowProps = flowNodeApiExp.getFlowProp(flow.getId());
        //  3.校验用户发起审批权限
        boolean hasAccess = hasAccess2Approve(flowProps, user.getUnitId(), userId);
        if (!hasAccess) {
            throw new GoodsException(GoodsErrorCode.NOT_RELEVANT_FLOW);
        }
        // 通过时间和次序得到该次下的所有申请id（已提交且未被拒绝）
        GoodsSetting setInfo = goodsSettingService.getSetByDate(goodsApproveVO.getTime());
        String batchNum = DateUtil.dateToString(setInfo.getDeadline(), "yyyyMM") + setInfo.getGoodsIndex();
        //  4.同步节点属性
        syncBudgetProjectFlow(flowProps, batchNum, userId);
        // 得到推送模板
        String inform = getInform(flowProps.get(0).getFlowNodeId()
                , FlowNodeNoticeState.DEFAULT_REMINDER.value());
        if (inform == null) {
            return;
        }
        // 修改推送模板
        inform = inform.replace("title"
                , "宣传影片" + batchNum + "批次");
        // 推送消息给发起人
        applicationNoticeService.saveApplicationNotice (
                ApplicationNoticeVO.builder()
                        .batchNum(batchNum)
                        .receiver(userId)
                        .promoter(userId)
                        .content(inform)
                        .flowTypeId(goodsApproveVO.getFlowTypeId())
                        .build());
        // 修改申领审批状态
        applicationMapper.updateByBatchNum(batchNum, 10);
        // 修改批次状态
        goodsSettingService.updateByBatchNum(batchNum);
    }

    @Override
    public void approve(ApplicationApproveVO approveVO, Integer userId) {
        // 查询当前节点信息
        ApplicationFlow currentFlow = applicationFlowService
                .getInfoById(approveVO.getId());
        // 项目签报id
        String batchNum = currentFlow.getBatchNum();
        // 当前节点id
        String currentFlowNodeId = currentFlow.getFlowNodeId();
        // 所有审批人
        List<ApplicationFlowNodeVO> approvers = approveVO.getApprovers();
        // 所有节点id
        List<String> flowNodes = approvers.stream().map(
                ApplicationFlowNodeVO::getFlowNodeId).collect(Collectors.toList());
        // 节点所对应的用户id
        // flowNodeId => userId
        Map<String, Integer> approverMap = new HashMap<>();
        for (ApplicationFlowNodeVO approver : approvers) {
            approverMap.put(approver.getFlowNodeId(), approver.getUserId());
        }
        Integer operation = null;
        Integer projectState = null;
        List<Integer> projectIds = new ArrayList<>();
        // 同意
        if (approveVO.getOperation().equals(OperationState.AGREE.value())) {
            operation = OperationState.AGREE.value();
            // 判断是否为最后一个节点，如果是，则更新项目流程状态
            if (isLastNode(currentFlowNodeId, flowNodes)) {
                projectState = ApplicationState.APPROVED.value();
            } else {
                // 获取用户的所有流程角色
                List<Long> flowRoleIds = flowRoleUserApiExp.getFlowRoleIdByUserId(userId);
                // 判断当前用户是否为分配者。如果是分配者，则判断是否已指定所有审批人
                if (flowRoleIds.contains(currentFlow.getAssigner())) {
                    // 判断是否所有审批人已指定
                    if (!isAllApproverAssigned(approvers)) {
                        throw new GoodsException(GoodsErrorCode.NOT_RELEVANT_FLOW);
                    }
                    // 更新各节点审批人
                    applicationFlowService.updateBatchByNodeId(approvers, batchNum);
                }
                // 如果不是分配者，则判断是否已指定下一个审批人
                else {
                    // 校验下一个节点的审批人是否已指定
                    Integer nextApprover = approverMap
                            .get(getNextNode(currentFlowNodeId, flowNodes));
                    if (nextApprover == null) {
                        throw new GoodsException(GoodsErrorCode.NOT_RELEVANT_FLOW);
                    }
                }
            }
        }
        // 拒绝
        else if (approveVO.getOperation().equals(OperationState.REJECT.value())) {
            operation = OperationState.REJECT.value();
            projectState = NodeState.APPROVE_REJECTED.value();
        }

        // 同意或者拒绝后对该签报的代办提醒进行删除
        applicationNoticeService.updateByBatchNum(batchNum);

        // 推送提醒
        assert operation != null;
        toInform(operation, approvers, userId, batchNum, currentFlowNodeId, flowNodes,
                approverMap, approveVO.getSuggestion());

        // 更新项目节点信息
        applicationFlowService.updateById(ApplicationFlow.builder()
                .operation(operation)
                .suggestion(approveVO.getSuggestion())
                .updateTime(new Date())
                .id(approveVO.getId())
                .build());

        // 更新项目的流程状态
        if (projectState != null) {
           applicationMapper.updateByBatchNum(batchNum, projectState);
        }
    }

    /**
     * 获取市场部或政企的汇总
     */
    private List<SummaryUnitGoodsVO> getUnitSummaryList(GoodsReqVO goodsReqVO, Integer type) {
        // 展示该次该单位下的申请汇总。
        return goodsService.selectUnitSummaryList(goodsReqVO, type);
    }

    /**
     * 判断当前用户是否有权限发起审批权限
     */
    private boolean hasAccess2Approve(List<FlowNodePropVO> flowProps, Integer userId, Integer unitId) {
        UnitTopVO topUnit = unitApiExp.getTopUnit();
        Integer benbu = topUnit.getBenbu();
        Integer hangzhou = topUnit.getHangzhou();
        List<Long> flowRoleIds = flowRoleUserApiExp.getFlowRoleIdByUserId(userId);
        // 第一个节点属性
        FlowNodePropVO firstNodeProp = flowProps.get(0);
        // 判断是有默认用户
        // 如果设定了默认用户，且为当前登录用户，则有发起权限
        if (firstNodeProp.getUserId() != null) {
            return firstNodeProp.getUserId().equals(userId);
        }
        // 如果没有设定默认用户，则通过流程角色判断
        else {
            // 如果角色范围为全杭州
            if (hangzhou.equals(firstNodeProp.getUnitId()) || firstNodeProp.getUnitId().equals(0)) {
                return flowRoleIds.contains(firstNodeProp.getFlowRoleId());
            }
            // 如果角色范围为本部
            else if (benbu.equals(firstNodeProp.getUnitId())) {
                List<Integer> unitIds = unitApiExp.getSubUnit(benbu);
                return unitIds.contains(unitId) && flowRoleIds
                        .contains(firstNodeProp.getFlowRoleId());
            }
            // 如果为确定某个单位
            else {
                // 必须单位和流程角色都匹配，才可判定为有发起权限
                return unitId.equals(firstNodeProp.getUnitId())
                        && flowRoleIds.contains(firstNodeProp.getFlowRoleId());
            }
        }
    }

    /**
     * 提醒信息
     */
    public String getInform(String flowNodeId, Integer state) {
        String inform = null;
        List<FlowNodeNoticeResVO> flowNodeNotices = flowNoticeApiExp
                .getFlowNodeNotice(flowNodeId);
        for (FlowNodeNoticeResVO flowNodeNotice : flowNodeNotices) {
            if (flowNodeNotice.getState().equals(state)) {
                inform = flowNodeNotice.getInform();
            }
        }
        return inform;
    }

    /**
     * 同步节点属性
     */
    private void syncBudgetProjectFlow(List<FlowNodePropVO> flowProps, String batchNum, Integer userId) {

        // 用来存储同步节点的list
        List<ApplicationFlow> list = new ArrayList<>();
        // 判断节点是否有保存属性
        for (FlowNodePropVO flowPropVO : flowProps) {
            if (flowPropVO.getIsJoin() == null || flowPropVO.getControlAccess() == null) {
                throw new GoodsException(GoodsErrorCode.NOT_RELEVANT_FLOW);
            }
        }
        // 判断第一个节点是否有默认用户，如果没有则为当前用户
        if (flowProps.get(0).getUserId() == null) {
            flowProps.get(0).setUserId(userId);
        }
        // 所以需要先清空节点，再同步节点。
        applicationFlowService.deleteByBatchNum(batchNum);
        for (FlowNodePropVO flowPropVO : flowProps) {
            list.add(ApplicationFlow.builder()
                    .flowNodeId(flowPropVO.getFlowNodeId())
                    .batchNum(batchNum)
                    .userId(flowPropVO.getUserId())
                    .flowRoleId(flowPropVO.getFlowRoleId())
                    .roleDesc(flowPropVO.getRoleDesc())
                    .controlAccess(flowPropVO.getControlAccess())
                    .isJoin(flowPropVO.getIsJoin())
                    .assigner(flowPropVO.getAssigner())
                    .operation(OperationState.UN_EXECUTED.value())
                    .build());
        }
        applicationFlowService.insertBatch(list);
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
    private boolean isAllApproverAssigned( List<ApplicationFlowNodeVO> approvers) {
        for (ApplicationFlowNodeVO approver : approvers) {
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
     * @param batchNum          批次号
     * @param currentFlowNodeId 当前的节点id
     * @param flowNodes         所有的节点id
     * @param approverMap       flowNodeId => userId
     * @param suggestion        意见
     */
    private void toInform(Integer operation, List<ApplicationFlowNodeVO> approvers,
                          Integer userId, String batchNum , String currentFlowNodeId, List<String> flowNodes,
                          Map<String, Integer> approverMap, String suggestion) {
        // 通过flowNodeId得到流程类型id
        Long flowTypeId =flowTypeApiExp.getIdByNodeId(flowNodes.get(0));
        List<Application> applicationList = applicationMapper.selectByBatchNum(batchNum);
        // 获取用户姓名
        UserInfo user = sysUserApiExp.getUserInfoById(userId);
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
                inform = inform.replace("title", "宣传用品"+batchNum+"费用签报");
                // 推送下一位审批者
                Integer nextApprover = approverMap.get(getNextNode(currentFlowNodeId, flowNodes));
                applicationNoticeService.saveApplicationNotice(
                        ApplicationNoticeVO.builder().batchNum(batchNum)
                                .receiver(nextApprover)
                                .promoter(userId)
                                .content(inform)
                                .flowTypeId(flowTypeId)
                                .build());
//                // 推送邮件
//                if (mailEnable) {
//                    // 下一节点审批人信息
//                    UserInfo nextApproverInfo = sysUserApiExp.getUserInfoById(nextApprover);
//                    // 推送内容
//                    String info = "宣传用品"+batchNum+"费用签报";
//                    mailApiExp.postMail(new MailVO(nextApproverInfo.getEmail(), nextApproverInfo.getNickName(), info));
//                }
            }


            inform = getInform(currentFlowNodeId,
                    FlowNodeNoticeState.COMPLETE_REMINDER.value());
            if (inform == null) {
                return;
            }
            inform = inform.replace("title", "宣传用品"+batchNum+"费用签报");
            inform = inform.replace("approve", user.getNickName());
            // 推送发起人
            applicationNoticeService.saveApplicationNotice(
                    ApplicationNoticeVO.builder().batchNum(batchNum)
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
            String replace = inform.replace("title", "宣传用品"+batchNum+"费用签报");
            inform = replace.replace("approve", user.getNickName());
            inform = inform.replace("cause", suggestion);
            applicationNoticeService.saveApplicationNotice(
                    ApplicationNoticeVO.builder().batchNum(batchNum)
                            .receiver(approvers.get(0).getUserId())
                            .promoter(userId)
                            .content(inform)
                            .flowTypeId(flowTypeId)
                            .build());

        }
    }
}
