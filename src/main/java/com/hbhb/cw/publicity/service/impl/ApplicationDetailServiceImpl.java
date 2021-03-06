package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.flowcenter.model.Flow;
import com.hbhb.cw.flowcenter.vo.FlowNodePropVO;
import com.hbhb.cw.publicity.enums.ApplicationState;
import com.hbhb.cw.publicity.enums.FlowNodeNoticeState;
import com.hbhb.cw.publicity.enums.GoodsType;
import com.hbhb.cw.publicity.enums.NodeState;
import com.hbhb.cw.publicity.enums.OperationState;
import com.hbhb.cw.publicity.enums.PublicityErrorCode;
import com.hbhb.cw.publicity.exception.PublicityException;
import com.hbhb.cw.publicity.mapper.ApplicationDetailMapper;
import com.hbhb.cw.publicity.mapper.ApplicationMapper;
import com.hbhb.cw.publicity.mapper.GoodsMapper;
import com.hbhb.cw.publicity.mapper.VerifyNoticeMapper;
import com.hbhb.cw.publicity.model.Application;
import com.hbhb.cw.publicity.model.ApplicationDetail;
import com.hbhb.cw.publicity.model.ApplicationFlow;
import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.cw.publicity.model.VerifyNotice;
import com.hbhb.cw.publicity.rpc.FlowApiExp;
import com.hbhb.cw.publicity.rpc.FlowNodePropApiExp;
import com.hbhb.cw.publicity.rpc.FlowNoticeApiExp;
import com.hbhb.cw.publicity.rpc.FlowRoleUserApiExp;
import com.hbhb.cw.publicity.rpc.FlowTypeApiExp;
import com.hbhb.cw.publicity.rpc.HallApiExp;
import com.hbhb.cw.publicity.rpc.SysDictApiExp;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.rpc.UnitApiExp;
import com.hbhb.cw.publicity.service.ApplicationDetailService;
import com.hbhb.cw.publicity.service.ApplicationFlowService;
import com.hbhb.cw.publicity.service.ApplicationNoticeService;
import com.hbhb.cw.publicity.service.GoodsSettingService;
import com.hbhb.cw.publicity.service.MailService;
import com.hbhb.cw.publicity.web.vo.ApplicationApproveVO;
import com.hbhb.cw.publicity.web.vo.ApplicationByUnitVO;
import com.hbhb.cw.publicity.web.vo.ApplicationFlowNodeVO;
import com.hbhb.cw.publicity.web.vo.ApplicationNoticeResVO;
import com.hbhb.cw.publicity.web.vo.GoodsApproveVO;
import com.hbhb.cw.publicity.web.vo.GoodsCheckerResVO;
import com.hbhb.cw.publicity.web.vo.GoodsCheckerVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.SummaryByUnitVO;
import com.hbhb.cw.publicity.web.vo.SummaryCondVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitApplicationVO;
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
import java.util.Set;
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
    @Resource
    private MailService mailService;
    @Resource
    private VerifyNoticeMapper verifyNoticeMapper;
    @Resource
    private HallApiExp hallApiExp;
    @Value("${mail.enable}")
    private Boolean mailEnable;

    @Override
    public SummaryUnitGoodsResVO getUnitGoodsList(GoodsReqVO goodsReqVO) {
        Integer hangzhou = UnitEnum.HANGZHOU.value();
        // ??????????????????????????????
        if (hangzhou.equals(goodsReqVO.getUnitId())) {
            goodsReqVO.setUnitId(null);
        }
        GoodsSetting goodsSetting = null;
        if (goodsReqVO.getTime() != null && goodsReqVO.getGoodsIndex() == null) {
            return new SummaryUnitGoodsResVO();
        }
        if (goodsReqVO.getTime() == null) {
            // ????????????????????????
            goodsSetting = goodsSettingService.getSetByDate(DateUtil.dateToString(new Date()));
            goodsReqVO.setTime(goodsSetting.getDeadline());
            goodsReqVO.setGoodsIndex(goodsSetting.getGoodsIndex());
        } else {
            goodsSetting = goodsSettingService.getByCond(goodsReqVO.getTime(), goodsReqVO.getGoodsIndex());
            if (goodsSetting == null) {
                return new SummaryUnitGoodsResVO();
            }
            goodsReqVO.setTime(goodsSetting.getDeadline());
        }
        if (goodsSetting.getDeadline() == null) {
            return new SummaryUnitGoodsResVO();
        }
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(goodsSetting.getDeadline()), "yyyyMM") + goodsReqVO.getGoodsIndex();
        // ????????????
        List<SummaryUnitApplicationVO> simplexList = getApplicationSum(batchNum, goodsReqVO.getUnitId(), GoodsType.BUSINESS_SIMPLEX.getValue());
        // ????????????
        List<SummaryUnitApplicationVO> singleList = getApplicationSum(batchNum, goodsReqVO.getUnitId(), GoodsType.FLYER_PAGE.getValue());
        // ??????goodsId??????unitName
        Map<Integer, SummaryUnitApplicationVO> map = new HashMap<>();
        for (SummaryUnitApplicationVO summaryUnitGoodsVO : simplexList) {
            // ????????????????????????????????????0
            summaryUnitGoodsVO.setSingleAmount(0L);
            map.put(summaryUnitGoodsVO.getUnitId(), summaryUnitGoodsVO);
        }
        if (singleList.size() == 0) {
            simplexList.addAll(singleList);
        } else {
            for (SummaryUnitApplicationVO cond : singleList) {
                // ????????????????????????????????????0
                cond.setSimplexAmount(0L);
                if (map.get(cond.getUnitId()) == null) {
                    simplexList.add(cond);
                } else {
                    map.get(cond.getUnitId()).setSingleAmount(cond.getSingleAmount());
                }
            }
        }
        goodsReqVO.setTime(goodsSetting.getDeadline());
        boolean flag = false;
        if (simplexList.size() != 0) {
            for (SummaryUnitApplicationVO summaryUnitGoodsVO : simplexList) {
                if (summaryUnitGoodsVO.getApprovedState().equals(NodeState.NOT_APPROVED.value())
                        || summaryUnitGoodsVO.getApprovedState().equals(NodeState.APPROVE_REJECTED.value())) {
                    flag = true;
                    break;
                }
            }
        }
        String deadline = goodsSetting.getDeadline();
        String time = goodsReqVO.getTime();
        // ?????????????????????????????????????????????
        if (goodsSetting.getIsEnd() != null ||
                DateUtil.stringToDate(deadline).getTime() < DateUtil.stringToDate(time).getTime()) {
            // ??????????????????????????????
            return new SummaryUnitGoodsResVO(simplexList, flag, batchNum);
        }
        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        for (int i = 0; i < simplexList.size(); i++) {
            simplexList.get(i).setLineNum(i + 1L);
            simplexList.get(i).setUnitName(unitMap.get(simplexList.get(i).getUnitId()));
        }
        // ????????????????????????????????????????????????
        return new SummaryUnitGoodsResVO(simplexList, flag, batchNum);
    }

    @Override
    public SummaryByUnitVO getInfoList(GoodsReqVO goodsReqVO) {
        Integer hangzhou = UnitEnum.HANGZHOU.value();
        if (hangzhou.equals(goodsReqVO.getUnitId())) {
            goodsReqVO.setUnitId(null);
        }
        String time = goodsReqVO.getTime();
        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        List<SummaryUnitGoodsVO> singList = getUnitSummaryList(goodsReqVO, GoodsType.FLYER_PAGE.getValue());
        for (int i = 0; i < singList.size(); i++) {
            singList.get(i).setLineNum(i + 1L);
            singList.get(i).setUnitName(unitMap.get(singList.get(i).getUnitId()));
        }
        if (time != null) {
            goodsReqVO.setTime(time);
        }
        List<SummaryUnitGoodsVO> simList = getUnitSummaryList(goodsReqVO, GoodsType.BUSINESS_SIMPLEX.getValue());
        for (int i = 0; i < simList.size(); i++) {
            simList.get(i).setLineNum(i + 1L);
            simList.get(i).setUnitName(unitMap.get(simList.get(i).getUnitId()));
        }
        return new SummaryByUnitVO(simList, singList);
    }

    @Override
    public List<UnitGoodsStateVO> getUnitGoodsStateList(GoodsReqVO goodsReqVO) {
        // ?????????????????????????????????????????????
        List<Integer> userVOList = flowRoleUserApiExp.getUserIdByRoleName("???????????????");
        List<Integer> userList = new ArrayList<>();
        for (Integer userId : userVOList) {
            userList.add(Math.toIntExact(userId));
        }
        List<UserInfo> userInfoList = sysUserApiExp.getUserInfoBatch(userList);
        Map<Integer, String> userMap = userInfoList.stream()
                .collect(Collectors.toMap(UserInfo::getId, UserInfo::getNickName));
        // ?????????????????????
        List<DictVO> dict = sysDictApiExp.getDict(TypeCode.PUBLICITY.value(),
                DictCode.PUBLICITY_APPLICATION_DETAIL_STATE.value());
        Map<String, String> dictMap = dict.stream().collect(Collectors.toMap(DictVO::getValue, DictVO::getLabel));
        String batchNum = getBatchNum(goodsReqVO);
        if (batchNum == null) {
            return new ArrayList<>();
        }
        // ???????????????????????????
        List<ApplicationByUnitVO> applicationByUnitVOList = applicationMapper.selectByUnit(goodsReqVO.getUnitId(), batchNum);
        // ????????????????????????????????????goodsId???
        Map<Integer, List<ApplicationByUnitVO>> map = applicationByUnitVOList.stream()
                .collect(Collectors.groupingBy(ApplicationByUnitVO::getUnitId));
        // ????????????????????????????????????????????????
        List<UnitGoodsStateVO> unitGoodsStateVOS = new ArrayList<>();
        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        List<Integer> unitIdList = unitApiExp.getAllUnitId();
        unitIdList.add(11);
        // ??????????????????????????????
        for (Integer unitId : unitIdList) {
            List<ApplicationByUnitVO> list = map.get(unitId);
            // ????????????????????????????????????????????????
            if (list != null && list.get(0).getState() == 0) {
                unitGoodsStateVOS.add(UnitGoodsStateVO.builder()
                        .unitName(unitMap.get(unitId))
                        .state(dictMap.get(String.valueOf(0))).build());
            }
            // ???????????????????????????????????????????????????
            else if (list != null) {
                UnitGoodsStateVO vo = new UnitGoodsStateVO();
                vo.setUnitName(unitMap.get(unitId));
                StringBuilder state = new StringBuilder();
                Map<String, Integer> checkerMap = new HashMap<>();
                // ??????state???????????????
                // ??????state??????????????????
                for (ApplicationByUnitVO appVO : list) {
                    if (3 == appVO.getState() && checkerMap.get(userMap.get(appVO.getChecker())) == null) {
                        checkerMap.put(userMap.get(appVO.getChecker()), appVO.getState());
                        state.append(userMap.get(appVO.getChecker())).append((dictMap.get(appVO.getState().toString()))).append(" ");
                    }
                }
                for (ApplicationByUnitVO appVO : list) {
                    if (0 == appVO.getState() && checkerMap.get(userMap.get(appVO.getChecker())) == null) {
                        checkerMap.put(userMap.get(appVO.getChecker()), appVO.getState());
                        state.append(userMap.get(appVO.getChecker())).append((dictMap.get(appVO.getState().toString()))).append(" ");
                    }
                }
                for (ApplicationByUnitVO appVO : list) {
                    if (1 == appVO.getState() && checkerMap.get(userMap.get(appVO.getChecker())) == null) {
                        checkerMap.put(userMap.get(appVO.getChecker()), appVO.getState());
                        state.append(userMap.get(appVO.getChecker())).append((dictMap.get(appVO.getState().toString()))).append(" ");
                    }
                }
                for (ApplicationByUnitVO appVO : list) {
                    if (2 == appVO.getState() && checkerMap.get(userMap.get(appVO.getChecker())) == null) {
                        checkerMap.put(userMap.get(appVO.getChecker()), appVO.getState());
                        state.append(userMap.get(appVO.getChecker())).append((dictMap.get(appVO.getState().toString()))).append(" ");
                    }
                }
                vo.setState(state.toString());
                unitGoodsStateVOS.add(vo);
            }
        }
        // ????????????id?????????????????????????????????????????????
        return unitGoodsStateVOS;
    }

    @Override
    public List<SummaryUnitGoodsVO> getUnitSummaryList(GoodsReqVO goodsReqVO, Integer type) {
        String batchNum = getBatchNum(goodsReqVO);
        // ??????????????????????????????????????????
        return applicationDetailMapper.selectSummaryUnitByType(SummaryCondVO.builder()
                .batchNum(batchNum)
                .unitId(goodsReqVO.getUnitId())
                .type(type)
                .build());
    }

    @Override
    public List<VerifyGoodsVO> getList(Integer userId) {
        Date date = new Date();
        // ?????????????????????????????????????????????????????????????????????
        GoodsSetting setting = goodsSettingService.getSetByDate(DateUtil.dateToString(date));
        if (setting == null) {
            throw new PublicityException(PublicityErrorCode.NOT_NUMBER_IN_MONTH);
        }
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(setting.getDeadline()), "yyyyMM") + setting.getGoodsIndex();
        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        // ???????????? ????????????????????????
        List<VerifyGoodsVO> verifyGoodsVOS = goodsMapper.selectVerifyList(userId, batchNum);
        for (VerifyGoodsVO verifyGoodsVO : verifyGoodsVOS) {
            verifyGoodsVO.setUnitName(unitMap.get(verifyGoodsVO.getUnitId()));
        }
        return verifyGoodsVOS;
    }

    @Override
    public List<VerifyHallGoodsVO> getHallList(GoodsCheckerVO goodsCheckerVO) {
        Date date = new Date();
        // ?????????????????????????????????????????????????????????????????????
        GoodsSetting setting = goodsSettingService.getSetByDate(DateUtil.dateToString(date));
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(setting.getDeadline()), "yyyyMM") + setting.getGoodsIndex();
        List<VerifyHallGoodsVO> list = goodsMapper.selectVerifyHallList(VerifyHallGoodsReqVO.builder()
                .goodsId(goodsCheckerVO.getGoodsId())
                .unitId(goodsCheckerVO.getUnitId())
                .batchNum(batchNum)
                .build()
        );
        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        // ?????????????????????????????????map
        Map<Integer, String> map = hallApiExp.selectHallByUnitId(11);
        for (VerifyHallGoodsVO verifyHallGoodsVO : list) {
            verifyHallGoodsVO.setUnitName(unitMap.get(verifyHallGoodsVO.getUnitId()));
            verifyHallGoodsVO.setHallName(map.get(Math.toIntExact(verifyHallGoodsVO.getHallId())));
        }
        return list;
    }

    @Override
    public void approveUnitGoods(GoodsCheckerResVO goodsCheckerResVO) {
        List<GoodsCheckerVO> goodsCheckerVOs = goodsCheckerResVO.getList();
        List<Integer> unitIds = new ArrayList<>();
        for (GoodsCheckerVO goodsCheckerVO : goodsCheckerVOs) {
            unitIds.add(goodsCheckerVO.getUnitId());
        }
        Date date = new Date();
        // ?????????????????????????????????????????????????????????????????????
        GoodsSetting setting = goodsSettingService.getSetByDate(DateUtil.dateToString(date));
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(setting.getDeadline()), "yyyyMM") + setting.getGoodsIndex();
        // ?????????????????????????????????
        List<Application> applications = applicationMapper.createLambdaQuery()
                .andIn(Application::getUnitId, unitIds)
                .andEq(Application::getBatchNum, batchNum)
                .select();
        // unitId => applicationId
        HashMap<Integer, List<Long>> map = new HashMap<>();
        for (Application application : applications) {
            if (map.get(application.getUnitId()) == null) {
                List<Long> applicationIds = new ArrayList<>();
                applicationIds.add(application.getId());
                map.put(application.getUnitId(), applicationIds);
            } else {
                map.get(application.getUnitId()).add(application.getId());
            }
        }
        // goodsId => unitId
        Map<Long, List<Integer>> goodsIdByUnitIdMap = new HashMap<>();
        for (GoodsCheckerVO goodsCheckerVO : goodsCheckerVOs) {
            if (goodsIdByUnitIdMap.get(goodsCheckerVO.getGoodsId()) == null) {
                List<Integer> unitIdList = new ArrayList<>();
                unitIdList.add(goodsCheckerVO.getUnitId());
                goodsIdByUnitIdMap.put(goodsCheckerVO.getGoodsId(), unitIdList);
            } else {
                goodsIdByUnitIdMap.get(goodsCheckerVO.getGoodsId()).add(goodsCheckerVO.getUnitId());
            }
        }
        Set<Long> goodsIds = goodsIdByUnitIdMap.keySet();
        // ????????????????????????????????????
        for (Long goodsId : goodsIds) {
            List<Integer> unitIdList = goodsIdByUnitIdMap.get(goodsId);
            List<Long> applicationIdList = new ArrayList<>();
            for (Integer unitId : unitIdList) {
                applicationIdList.addAll(map.get(unitId));
            }
            applicationDetailMapper.createLambdaQuery()
                    .andIn(ApplicationDetail::getApplicationId, applicationIdList)
                    .andEq(ApplicationDetail::getGoodsId, goodsId)
                    .updateSelective(ApplicationDetail.builder()
                            .state(goodsCheckerResVO.getDetailState())
                            .build());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toApprover(GoodsApproveVO goodsApproveVO, Integer userId) {
        GoodsSetting goodsSetting = null;
        if (goodsApproveVO.getTime() != null && goodsApproveVO.getGoodsIndex() == null) {
            return;
        }
        if (goodsApproveVO.getTime() == null) {
            // ????????????????????????
            goodsSetting = goodsSettingService.getSetByDate(DateUtil.dateToString(new Date()));
        } else {
            goodsSetting = goodsSettingService.getByCond(goodsApproveVO.getTime(), goodsApproveVO.getGoodsIndex());
        }
        goodsApproveVO.setTime(goodsSetting.getDeadline());
        if (goodsApproveVO.getGoodsIndex() == null) {
            goodsApproveVO.setGoodsIndex(goodsSetting.getGoodsIndex());
        }
        // ????????????????????????
        UserInfo user = sysUserApiExp.getUserInfoById(userId);
        //  2.????????????id ??????????????????????????????id
        List<Flow> flows = flowApiExp.getFlowsByTypeId(goodsApproveVO.getFlowTypeId());
        if (flows == null || flows.size() != 1) {
            throw new PublicityException(PublicityErrorCode.NOT_HAVE_FLOW);
        }
        Flow flow = flows.get(0);
        List<FlowNodePropVO> flowProps = flowNodePropApiExp.getNodeProps(flow.getId());
        //  3.??????????????????????????????
        boolean hasAccess = hasAccess2Approve(flowProps, userId, user.getUnitId());
        if (!hasAccess) {
            throw new PublicityException(PublicityErrorCode.NOT_POWER_TO_APPROVER);
        }
        // ???????????????????????????????????????????????????id??????????????????????????????
        GoodsSetting setInfo = goodsSettingService.getSetByDate(goodsApproveVO.getTime());
        // ???????????????
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(setInfo.getDeadline()), "yyyyMM")
                + setInfo.getGoodsIndex();
        //  4.??????????????????
        syncAppliactionFlow(flowProps, batchNum, userId, user.getUnitId());
        // ??????????????????
        String inform = getInform(flowProps.get(0).getFlowNodeId()
                , FlowNodeNoticeState.DEFAULT_REMINDER.value());
        if (inform == null) {
            return;
        }
        // ??????????????????
        inform = inform.replace("title"
                , "????????????" + batchNum + "??????");
        // ????????????????????????
        applicationNoticeService.saveApplicationNotice(
                ApplicationNoticeResVO.builder()
                        .batchNum(batchNum)
                        .receiver(userId)
                        .promoter(userId)
                        .content(inform)
                        .flowTypeId(goodsApproveVO.getFlowTypeId())
                        .state(0)
                        .build());
        // ????????????????????????
        List<Application> applicationList = applicationMapper.createLambdaQuery()
                .andEq(Application::getBatchNum, batchNum).select();
        List<Long> applicationIdList = new ArrayList<>();
        for (Application application : applicationList) {
            applicationIdList.add(application.getId());
        }
        List<Integer> stateList = new ArrayList<>();
        stateList.add(1);
        stateList.add(2);
        // ???????????????????????????????????????????????????????????????,????????????????????????
        applicationDetailMapper.createLambdaQuery()
                .andIn(ApplicationDetail::getApplicationId, applicationIdList)
                .andEq(ApplicationDetail::getUnderUnitId, goodsApproveVO.getUnderUnitId())
                .andIn(ApplicationDetail::getState, stateList)
                .updateSelective(ApplicationDetail.builder()
                        .approvedState(NodeState.APPROVING.value())
                        .state(2)
                        .build());
        // ??????????????????
        goodsSettingService.updateByBatchNum(batchNum);
        // ??????????????????
        verifyNoticeMapper.createLambdaQuery()
                .andEq(VerifyNotice::getBatchNum, batchNum)
                .andEq(VerifyNotice::getUnitId, goodsApproveVO.getUnderUnitId())
                .updateSelective(VerifyNotice.builder().state(1).build());
    }

    @Override
    public void approve(ApplicationApproveVO approveVO, Integer userId) {
        // ????????????????????????
        ApplicationFlow currentFlow = applicationFlowService
                .getInfoById(approveVO.getId());
        // ??????????????????????????????
        if (!currentFlow.getUserId().equals(userId)) {
            throw new PublicityException(PublicityErrorCode.LOCK_OF_APPROVAL_ROLE);
        }
        // ?????????????????????
        String batchNum = currentFlow.getBatchNum();
        // ????????????id
        String currentFlowNodeId = currentFlow.getFlowNodeId();
        // ???????????????
        List<ApplicationFlowNodeVO> approvers = approveVO.getApprovers();
        // ????????????id
        List<String> flowNodes = approvers.stream().map(
                ApplicationFlowNodeVO::getFlowNodeId).collect(Collectors.toList());
        // ????????????????????????id
        // flowNodeId => userId
        Map<String, Integer> approverMap = new HashMap<>();
        for (ApplicationFlowNodeVO approver : approvers) {
            approverMap.put(approver.getFlowNodeId(), approver.getUserId());
        }
        Integer operation = null;
        Integer projectState = null;
        // ??????
        if (approveVO.getOperation().equals(OperationState.AGREE.value())) {
            operation = OperationState.AGREE.value();
            // ???????????????????????????????????????????????????????????????????????????
            if (isLastNode(currentFlowNodeId, flowNodes)) {
                projectState = ApplicationState.APPROVED.value();
            } else {
                // ?????????????????????????????????
                List<Long> flowRoleIds = flowRoleUserApiExp.getRoleIdByUserId(userId);
                // ???????????????????????????????????????????????????????????????????????????????????????????????????
                if (flowRoleIds.contains(currentFlow.getAssigner())) {
                    // ????????????????????????????????????
                    if (!isAllApproverAssigned(approvers)) {
                        throw new PublicityException(PublicityErrorCode.NOT_RELEVANT_FLOW);
                    }
                    // ????????????????????????
                    applicationFlowService.updateBatchByNodeId(approvers, batchNum);
                }
                // ??????????????????????????????????????????????????????????????????
                else {
                    // ????????????????????????????????????????????????
                    Integer nextApprover = approverMap
                            .get(getNextNode(currentFlowNodeId, flowNodes));
                    if (nextApprover == null) {
                        throw new PublicityException(PublicityErrorCode.NOT_RELEVANT_FLOW);
                    }
                }
            }
        }
        // ??????
        else if (approveVO.getOperation().equals(OperationState.REJECT.value())) {
            operation = OperationState.REJECT.value();
            projectState = NodeState.APPROVE_REJECTED.value();
        }

        // ????????????????????????????????????????????????????????????
        applicationNoticeService.updateByBatchNum(batchNum, approveVO.getUnderUnitId());

        // ????????????
        assert operation != null;
        toInform(operation, approvers, userId, batchNum, currentFlowNodeId, flowNodes,
                approverMap, approveVO.getSuggestion(), approveVO.getUnderUnitId());

        // ????????????????????????
        applicationFlowService.updateById(ApplicationFlow.builder()
                .operation(operation)
                .suggestion(approveVO.getSuggestion())
                .updateTime(DateUtil.dateToString(new Date()))
                .id(approveVO.getId())
                .batchNum(batchNum)
                .build());

        // ???????????????????????????
        if (projectState != null) {
            // ????????????id
            List<Application> applicationList = applicationMapper.createLambdaQuery()
                    .andEq(Application::getBatchNum, batchNum).select();
            List<Long> applicationIdList = new ArrayList<>();
            for (Application application : applicationList) {
                applicationIdList.add(application.getId());
            }
            // ???????????????????????????????????????????????????????????????
            applicationDetailMapper.createLambdaQuery()
                    .andIn(ApplicationDetail::getApplicationId, applicationIdList)
                    .andEq(ApplicationDetail::getUnderUnitId, approveVO.getUnderUnitId())
                    .updateSelective(ApplicationDetail.builder()
                            .approvedState(projectState)
                            .build());
        }
    }


    /**
     * ???????????????????????????????????????????????????
     */
    private boolean hasAccess2Approve(List<FlowNodePropVO> flowProps, Integer userId, Integer unitId) {
        Integer benbu = UnitEnum.BENBU.value();
        Integer hangzhou = UnitEnum.HANGZHOU.value();
        List<Long> flowRoleIds = flowRoleUserApiExp.getRoleIdByUserId(userId);
        // ?????????????????????
        FlowNodePropVO firstNodeProp = flowProps.get(0);
        // ????????????????????????
        // ???????????????????????????????????????????????????????????????????????????
        if (firstNodeProp.getUserId() != null) {
            return firstNodeProp.getUserId().equals(userId);
        }
        // ????????????????????????????????????????????????????????????
        else {
            // ??????????????????????????????
            if (hangzhou.equals(firstNodeProp.getUnitId()) || firstNodeProp.getUnitId().equals(0)) {
                return flowRoleIds.contains(firstNodeProp.getFlowRoleId());
            }
            // ???????????????????????????
            else if (benbu.equals(firstNodeProp.getUnitId())) {
                List<Integer> unitIds = unitApiExp.getSubUnit(benbu);
                return unitIds.contains(unitId) && flowRoleIds
                        .contains(firstNodeProp.getFlowRoleId());
            }
            // ???????????????????????????
            else {
                // ?????????????????????????????????????????????????????????????????????
                return unitId.equals(firstNodeProp.getUnitId())
                        && flowRoleIds.contains(firstNodeProp.getFlowRoleId());
            }
        }
    }

    /**
     * ????????????
     */
    public String getInform(String flowNodeId, Integer state) {
        return flowNoticeApiExp.getInform(flowNodeId, state);
    }

    /**
     * ??????????????????
     */
    private void syncAppliactionFlow(List<FlowNodePropVO> flowProps, String batchNum, Integer userId, Integer unitId) {

        // ???????????????????????????list
        List<ApplicationFlow> list = new ArrayList<>();
        // ?????????????????????????????????
        for (FlowNodePropVO flowPropVO : flowProps) {
            if (flowPropVO.getIsJoin() == null || flowPropVO.getControlAccess() == null) {
                throw new PublicityException(PublicityErrorCode.NOT_RELEVANT_FLOW);
            }
        }
        // ???????????????????????????????????????????????????????????????????????????
        if (flowProps.get(0).getUserId() == null) {
            flowProps.get(0).setUserId(userId);
        }
        // ????????????????????????????????????????????????
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
                    .unitId(unitId)
                    .build());
        }
        applicationFlowService.insertBatch(list);
    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    private boolean isLastNode(String currentFlowNodeId, List<String> flowNodeIds) {
        if (CollectionUtils.isEmpty(flowNodeIds)) {
            return false;
        }
        return currentFlowNodeId.equals(flowNodeIds.get(flowNodeIds.size() - 1));
    }

    /**
     * ?????????????????????????????????????????????
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
     * ????????????????????????????????????
     */
    private String getNextNode(String currentFlowNodeId, List<String> flowNodeIds) {
        if (!flowNodeIds.contains(currentFlowNodeId) || isLastNode(currentFlowNodeId,
                flowNodeIds)) {
            return null;
        }
        return flowNodeIds.get(flowNodeIds.indexOf(currentFlowNodeId) + 1);
    }

    /**
     * ???????????????
     *
     * @param operation         ???????????????1??????0?????????
     * @param approvers         ???????????????
     * @param userId            ??????id
     * @param batchNum          ?????????
     * @param currentFlowNodeId ???????????????id
     * @param flowNodes         ???????????????id
     * @param approverMap       flowNodeId => userId
     * @param suggestion        ??????
     */
    private void toInform(Integer operation, List<ApplicationFlowNodeVO> approvers,
                          Integer userId, String batchNum, String currentFlowNodeId, List<String> flowNodes,
                          Map<String, Integer> approverMap, String suggestion, Integer underUnitId) {
        // ??????flowNodeId??????????????????id
        Long flowTypeId = flowTypeApiExp.getTypeIdByNode(flowNodes.get(0));
        // ??????????????????
        UserInfo user = sysUserApiExp.getUserInfoById(userId);
        // ????????????(??????)
        if (operation.equals(OperationState.AGREE.value())) {
            // ???????????????????????????
            String inform;
            // ?????????????????????????????????????????????????????????
            if (!approvers.get(approvers.size() - 1).getUserId().equals(userId)) {
                inform = getInform(currentFlowNodeId, FlowNodeNoticeState.DEFAULT_REMINDER.value());
                if (inform == null) {
                    return;
                }
                inform = inform.replace("title", "????????????" + batchNum + "????????????");
                // ????????????????????????
                Integer nextApprover = approverMap.get(getNextNode(currentFlowNodeId, flowNodes));
                applicationNoticeService.saveApplicationNotice(
                        ApplicationNoticeResVO.builder().batchNum(batchNum)
                                .receiver(nextApprover)
                                .promoter(userId)
                                .content(inform)
                                .flowTypeId(flowTypeId)
                                .state(0)
                                .underUnitId(underUnitId)
                                .build());
                // ????????????
                if (mailEnable) {
                    // ???????????????????????????
                    UserInfo nextApproverInfo = sysUserApiExp.getUserInfoById(nextApprover);
                    // ????????????
                    String info = "????????????" + batchNum + "????????????";
                    mailService.postMail(nextApproverInfo.getEmail(), nextApproverInfo.getNickName(), info);
                }
            }


            inform = getInform(currentFlowNodeId,
                    FlowNodeNoticeState.COMPLETE_REMINDER.value());
            if (inform == null) {
                return;
            }
            inform = inform.replace("title", "????????????" + batchNum + "????????????");
            inform = inform.replace("approve", user.getNickName());
            // ???????????????
            applicationNoticeService.saveApplicationNotice(
                    ApplicationNoticeResVO.builder().batchNum(batchNum)
                            .receiver(approvers.get(0).getUserId())
                            .promoter(userId)
                            .content(inform)
                            .flowTypeId(flowTypeId)
                            .underUnitId(underUnitId)
                            .state(0)
                            .build());
        }
        // ??????
        else {
            String inform = getInform(currentFlowNodeId,
                    FlowNodeNoticeState.REJECT_REMINDER.value());
            if (inform == null) {
                return;
            }
            String replace = inform.replace("title", "????????????" + batchNum + "????????????");
            inform = replace.replace("approve", user.getNickName());
            inform = inform.replace("cause", suggestion);
            applicationNoticeService.saveApplicationNotice(
                    ApplicationNoticeResVO.builder().batchNum(batchNum)
                            .receiver(approvers.get(0).getUserId())
                            .promoter(userId)
                            .content(inform)
                            .flowTypeId(flowTypeId)
                            .underUnitId(underUnitId)
                            .state(0)
                            .build());

        }
    }

    /**
     * ????????????
     */
    private List<SummaryUnitApplicationVO> getApplicationSum(String batchNum, Integer unitId, Integer type) {
        // ??????????????????????????????????????????
        return applicationDetailMapper.selectApplicationSumByType(SummaryCondVO.builder()
                .batchNum(batchNum)
                .unitId(unitId)
                .type(type)
                .build());
    }

    private String getBatchNum(GoodsReqVO goodsReqVO) {
        Integer hangzhou = UnitEnum.HANGZHOU.value();
        // ??????????????????????????????
        if (hangzhou.equals(goodsReqVO.getUnitId())) {
            goodsReqVO.setUnitId(null);
        }
        GoodsSetting goodsSetting = null;
        if (goodsReqVO.getTime() != null && goodsReqVO.getGoodsIndex() == null) {
            return null;
        }
        if (goodsReqVO.getTime() == null) {
            // ????????????????????????
            goodsSetting = goodsSettingService.getSetByDate(DateUtil.dateToString(new Date()));
            goodsReqVO.setTime(goodsSetting.getDeadline());
            goodsReqVO.setGoodsIndex(goodsSetting.getGoodsIndex());
        } else {
            goodsSetting = goodsSettingService.getByCond(goodsReqVO.getTime(), goodsReqVO.getGoodsIndex());
            if (goodsSetting == null) {
                return null;
            }
            goodsReqVO.setTime(goodsSetting.getDeadline());
        }
        if (goodsSetting.getDeadline() == null) {
            return null;
        }
        return DateUtil.dateToString(DateUtil.stringToDate(goodsSetting.getDeadline()), "yyyyMM") + goodsReqVO.getGoodsIndex();
    }
}
