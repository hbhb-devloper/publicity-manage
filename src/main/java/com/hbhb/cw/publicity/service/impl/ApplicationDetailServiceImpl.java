package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.flowcenter.model.Flow;
import com.hbhb.cw.flowcenter.vo.FlowNodeNoticeVO;
import com.hbhb.cw.flowcenter.vo.FlowNodePropVO;
import com.hbhb.cw.publicity.enums.ApplicationState;
import com.hbhb.cw.publicity.enums.FlowNodeNoticeState;
import com.hbhb.cw.publicity.enums.GoodsErrorCode;
import com.hbhb.cw.publicity.enums.GoodsType;
import com.hbhb.cw.publicity.enums.NodeState;
import com.hbhb.cw.publicity.enums.OperationState;
import com.hbhb.cw.publicity.exception.GoodsException;
import com.hbhb.cw.publicity.mapper.ApplicationDetailMapper;
import com.hbhb.cw.publicity.mapper.ApplicationMapper;
import com.hbhb.cw.publicity.mapper.GoodsMapper;
import com.hbhb.cw.publicity.model.Application;
import com.hbhb.cw.publicity.model.ApplicationDetail;
import com.hbhb.cw.publicity.model.ApplicationFlow;
import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.cw.publicity.rpc.FlowApiExp;
import com.hbhb.cw.publicity.rpc.FlowNodeApiExp;
import com.hbhb.cw.publicity.rpc.FlowNodePropApiExp;
import com.hbhb.cw.publicity.rpc.FlowNoticeApiExp;
import com.hbhb.cw.publicity.rpc.FlowRoleUserApiExp;
import com.hbhb.cw.publicity.rpc.FlowTypeApiExp;
import com.hbhb.cw.publicity.rpc.SysDictApiExp;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.rpc.UnitApiExp;
import com.hbhb.cw.publicity.service.ApplicationDetailService;
import com.hbhb.cw.publicity.service.ApplicationFlowService;
import com.hbhb.cw.publicity.service.ApplicationNoticeService;
import com.hbhb.cw.publicity.service.GoodsSettingService;
import com.hbhb.cw.publicity.web.vo.ApplicationApproveVO;
import com.hbhb.cw.publicity.web.vo.ApplicationByUnitVO;
import com.hbhb.cw.publicity.web.vo.ApplicationFlowNodeVO;
import com.hbhb.cw.publicity.web.vo.ApplicationNoticeVO;
import com.hbhb.cw.publicity.web.vo.GoodsApproveVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.SummaryCondVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsVO;
import com.hbhb.cw.publicity.web.vo.UnitGoodsStateVO;
import com.hbhb.cw.publicity.web.vo.VerifyGoodsVO;
import com.hbhb.cw.publicity.web.vo.VerifyHallGoodsReqVO;
import com.hbhb.cw.publicity.web.vo.VerifyHallGoodsVO;
import com.hbhb.cw.systemcenter.enums.DictCode;
import com.hbhb.cw.systemcenter.enums.TypeCode;
import com.hbhb.cw.systemcenter.enums.UnitEnum;
import com.hbhb.cw.systemcenter.vo.DictVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;

import org.springframework.beans.factory.annotation.Value;
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

import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-12-14
 */
@Slf4j
@Service
public class ApplicationDetailServiceImpl implements ApplicationDetailService {

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
    private UnitApiExp unitApiExp;
    @Resource
    private SysUserApiExp sysUserApiExp;
    @Resource
    private ApplicationDetailMapper applicationDetailMapper;
    @Resource
    private ApplicationFlowService applicationFlowService;
    @Resource
    private ApplicationNoticeService applicationNoticeService;
    @Resource
    private ApplicationMapper applicationMapper;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private SysDictApiExp sysDictApiExp;
    @Resource
    private FlowNodePropApiExp flowNodePropApiExp;
    @Resource
    private FlowApiExp flowApiExp;
    @Value("${cw.flow-role.name}")
    private String name;

    @Override
    public SummaryUnitGoodsResVO getUnitGoodsList(GoodsReqVO goodsReqVO) {
        String date = goodsReqVO.getTime();
        // 与截止时间对比，判断为第几月第几次
        GoodsSetting goodsSetting = goodsSettingService.getSetByDate(goodsReqVO.getTime());
        if (goodsSetting == null) {
            return new SummaryUnitGoodsResVO();
        }
        List<SummaryUnitGoodsVO> simSummaryList = getUnitSummaryList(goodsReqVO,0);

        List<SummaryUnitGoodsVO> singSummaryList = getUnitSummaryList(goodsReqVO, 1);
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
        if (goodsSetting.getIsEnd() != null ||
                DateUtil.stringToDate(goodsSetting.getDeadline()).getTime() < DateUtil.stringToDate(date).getTime()) {
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
        // 通过流程角色名称得到该角色用户
        List<Integer> userList = flowRoleUserApiExp.getUserIdByRoleName(name);
        List<UserInfo> userInfoList = sysUserApiExp.getUserInfoBatch(userList);
        Map<Integer, String> userMap = userInfoList.stream()
                .collect(Collectors.toMap(UserInfo::getId, UserInfo::getNickName));
        // 获取审核员审核
        List<DictVO> dict = sysDictApiExp.getDict(TypeCode.PUBLICITY.value(),
                DictCode.PUBLICITY_APPLICATION_DETAIL_STATE.value());
        Map<String, String> dictMap = dict.stream().collect(Collectors.toMap(DictVO::getValue, DictVO::getLabel));
        // 用sql语句完成
        List<ApplicationByUnitVO> applicationByUnitVOList = applicationMapper.selectByUnit(goodsReqVO);
        // 得到所有公司下所有货物（goodsId）
        Map<Integer, List<ApplicationByUnitVO>> map = applicationByUnitVOList.stream()
                .collect(Collectors.groupingBy(ApplicationByUnitVO::getUnitId));
        // 得到该货物的物料审核员和审核状态
        List<UnitGoodsStateVO> unitGoodsStateVOS = new ArrayList<>();
        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        List<Integer> unitIdList = unitApiExp.getAllUnitId();
        // 得到各单位的审核状态
        for (Integer unitId : unitIdList) {
            List<ApplicationByUnitVO> list = map.get(unitId);
            // 如果改单位能够申领分公司却未提交
            if (list != null && list.get(0).getState() == 0) {
                unitGoodsStateVOS.add(UnitGoodsStateVO.builder()
                        .unitName(unitMap.get(unitId))
                        .state(dictMap.get(0)).build());
            }
            // 获取该单位下的所有审核人的审核状况
            else if (list != null) {
                UnitGoodsStateVO vo = new UnitGoodsStateVO();
                vo.setUnitName(unitMap.get(unitId));
                StringBuilder state = new StringBuilder();
                for (ApplicationByUnitVO appVO : list) {
                    state.append(userMap.get(appVO.getChecker())).append(appVO.getState());
                }
                vo.setState(state.toString());
            }
        }
        // 通过单位id得到该分公司下的所有单位和状态
        return unitGoodsStateVOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toApprover(GoodsApproveVO goodsApproveVO, Integer userId) {
        // 获取登入用户单位
        UserInfo user = sysUserApiExp.getUserInfoById(userId);
        //  2.获取流程id 通过流程状态活动流程id
        List<Flow> flows = flowApiExp.getFlowsByTypeId(goodsApproveVO.getFlowTypeId());
        if (flows == null || flows.size() != 1) {
            throw new GoodsException(GoodsErrorCode.NOT_RELEVANT_FLOW);
        }
        Flow flow = flows.get(0);
        List<FlowNodePropVO> flowProps = flowNodePropApiExp.getNodeProps(flow.getId());
        //  3.校验用户发起审批权限
        boolean hasAccess = hasAccess2Approve(flowProps, user.getUnitId(), userId);
        if (!hasAccess) {
            throw new GoodsException(GoodsErrorCode.NOT_RELEVANT_FLOW);
        }
        // 通过时间和次序得到该次下的所有申请id（已提交且未被拒绝）
        GoodsSetting setInfo = goodsSettingService.getSetByDate(goodsApproveVO.getTime());
        String batchNum = setInfo.getDeadline() + setInfo.getGoodsIndex();
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
        applicationNoticeService.saveApplicationNotice(
                ApplicationNoticeVO.builder()
                        .batchNum(batchNum)
                        .receiver(userId)
                        .promoter(userId)
                        .content(inform)
                        .flowTypeId(goodsApproveVO.getFlowTypeId())
                        .build());
        // 修改申领审批状态
        applicationMapper.createLambdaQuery()
                .andEq(Application::getBatchNum, batchNum)
                .updateSelective(Application.builder()
                        .batchNum(batchNum)
                        .approvedState(10)
                        .build());
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
                List<Long> flowRoleIds = flowRoleUserApiExp.getRoleIdByUserId(userId);
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
            applicationMapper.createLambdaQuery()
                    .andEq(Application::getBatchNum, batchNum)
                    .updateSelective(Application.builder()
                            .batchNum(batchNum)
                            .approvedState(projectState)
                            .build());
        }
    }

    @Override
    public List<VerifyGoodsVO> getVerifyList(Integer userId) {
        UserInfo user = sysUserApiExp.getUserInfoById(userId);
        Date date = new Date();
        // 通过此刻时间与截止时间对比，判断为第几月第几次
        GoodsSetting setting = goodsSettingService.getSetByDate(DateUtil.dateToString(date));
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(setting.getDeadline()),"yyyyMM")+setting.getGoodsIndex();
        // 通过时间 ，每次获取其列表
        return goodsMapper.selectVerifyList(userId, batchNum);
    }

    @Override
    public List<VerifyHallGoodsVO> getInfoList(Integer unitId, Long goodsId) {
        Date date = new Date();
        // 通过此刻时间与截止时间对比，判断为第几月第几次
        GoodsSetting setting = goodsSettingService.getSetByDate(DateUtil.dateToString(date));
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(setting.getDeadline()),"yyyyMM")+setting.getGoodsIndex();
        return goodsMapper.selectVerifyHallList(VerifyHallGoodsReqVO.builder()
                .goodsId(goodsId)
                .unitId(unitId)
                .batchNum(batchNum)
                .build()
        );
    }

    @Override
    public void approveUnitGoods(Integer unitId, Long goodsId) {
        List<VerifyHallGoodsVO> infoList = getInfoList(unitId, goodsId);
        List<Long> ids = new ArrayList<>();
        for (VerifyHallGoodsVO cond : infoList) {
            ids.add(cond.getApplicationDetailId());
        }
        applicationDetailMapper.createLambdaQuery()
                .andIn(ApplicationDetail::getId,ids)
                .updateSelective(ApplicationDetail.builder().state(2).build());
    }

    @Override
    public List<SummaryUnitGoodsVO> getUnitSummaryList(GoodsReqVO goodsReqVO, Integer type) {
        String date = goodsReqVO.getTime();
        // 通过此刻时间与截止时间对比，判断为第几月第几次
        GoodsSetting goodsSetting = goodsSettingService.getSetByDate(goodsReqVO.getTime());
        if (goodsSetting==null){
            return new  ArrayList<SummaryUnitGoodsVO>();
        }
        goodsReqVO.setTime(goodsSetting.getDeadline());
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(goodsReqVO.getTime()),"yyyyMM")+goodsReqVO.getGoodsIndex();

        // 展示该次该单位下的申请汇总。
        return goodsMapper.selectSummaryUnitByType(SummaryCondVO.builder()
                .batchNum(batchNum)
                .unitId(goodsReqVO.getUnitId())
                .type(type)
                .build());
    }

    /**
     * 判断当前用户是否有权限发起审批权限
     */
    private boolean hasAccess2Approve(List<FlowNodePropVO> flowProps, Integer userId, Integer unitId) {
        Integer benbu = UnitEnum.BENBU.value();
        Integer hangzhou = UnitEnum.HANGZHOU.value();
        List<Long> flowRoleIds = flowRoleUserApiExp.getRoleIdByUserId(userId);
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
        List<FlowNodeNoticeVO> flowNodeNotices = flowNoticeApiExp
                .getNodeNoticeList(flowNodeId);
        for (FlowNodeNoticeVO flowNodeNotice : flowNodeNotices) {
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
    private boolean isAllApproverAssigned(List<ApplicationFlowNodeVO> approvers) {
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
                          Integer userId, String batchNum, String currentFlowNodeId, List<String> flowNodes,
                          Map<String, Integer> approverMap, String suggestion) {
        // 通过flowNodeId得到流程类型id
        Long flowTypeId = flowTypeApiExp.getTypeIdByNode(flowNodes.get(0));
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
                inform = inform.replace("title", "宣传用品" + batchNum + "费用签报");
                // 推送下一位审批者
                Integer nextApprover = approverMap.get(getNextNode(currentFlowNodeId, flowNodes));
                applicationNoticeService.saveApplicationNotice(
                        ApplicationNoticeVO.builder().batchNum(batchNum)
                                .receiver(nextApprover)
                                .promoter(userId)
                                .content(inform)
                                .flowTypeId(flowTypeId)
                                .build());
                // 推送邮件
//                if (mailEnable) {
//                    // 下一节点审批人信息
//                    UserInfo nextApproverInfo = sysUserApiExp.getUserInfoById(nextApprover);
//                    // 推送内容
//                    String info = "宣传用品" + batchNum + "费用签报";
//                    mailApiExp.postMail(new MailVO(nextApproverInfo.getEmail(), nextApproverInfo.getNickName(), info));
//                }
            }


            inform = getInform(currentFlowNodeId,
                    FlowNodeNoticeState.COMPLETE_REMINDER.value());
            if (inform == null) {
                return;
            }
            inform = inform.replace("title", "宣传用品" + batchNum + "费用签报");
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
            String replace = inform.replace("title", "宣传用品" + batchNum + "费用签报");
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
