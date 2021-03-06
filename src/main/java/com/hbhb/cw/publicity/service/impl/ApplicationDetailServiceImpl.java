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
        // 如果为杭州则能看全部
        if (hangzhou.equals(goodsReqVO.getUnitId())) {
            goodsReqVO.setUnitId(null);
        }
        GoodsSetting goodsSetting = null;
        if (goodsReqVO.getTime() != null && goodsReqVO.getGoodsIndex() == null) {
            return new SummaryUnitGoodsResVO();
        }
        if (goodsReqVO.getTime() == null) {
            // 通过时间判断批次
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
        // 业务单式
        List<SummaryUnitApplicationVO> simplexList = getApplicationSum(batchNum, goodsReqVO.getUnitId(), GoodsType.BUSINESS_SIMPLEX.getValue());
        // 宣传单页
        List<SummaryUnitApplicationVO> singleList = getApplicationSum(batchNum, goodsReqVO.getUnitId(), GoodsType.FLYER_PAGE.getValue());
        // 通过goodsId得到unitName
        Map<Integer, SummaryUnitApplicationVO> map = new HashMap<>();
        for (SummaryUnitApplicationVO summaryUnitGoodsVO : simplexList) {
            // 业务单式下宣传单页因都为0
            summaryUnitGoodsVO.setSingleAmount(0L);
            map.put(summaryUnitGoodsVO.getUnitId(), summaryUnitGoodsVO);
        }
        if (singleList.size() == 0) {
            simplexList.addAll(singleList);
        } else {
            for (SummaryUnitApplicationVO cond : singleList) {
                // 宣传单页下业务单式因都为0
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
        // 得到第几次，判断此次是否结束。
        if (goodsSetting.getIsEnd() != null ||
                DateUtil.stringToDate(deadline).getTime() < DateUtil.stringToDate(time).getTime()) {
            // 如果结束审核提交置灰
            return new SummaryUnitGoodsResVO(simplexList, flag, batchNum);
        }
        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        for (int i = 0; i < simplexList.size(); i++) {
            simplexList.get(i).setLineNum(i + 1L);
            simplexList.get(i).setUnitName(unitMap.get(simplexList.get(i).getUnitId()));
        }
        // 展示该次该管理部门下的申请汇总。
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
        // 通过流程角色名称得到该角色用户
        List<Integer> userVOList = flowRoleUserApiExp.getUserIdByRoleName("物料审核员");
        List<Integer> userList = new ArrayList<>();
        for (Integer userId : userVOList) {
            userList.add(Math.toIntExact(userId));
        }
        List<UserInfo> userInfoList = sysUserApiExp.getUserInfoBatch(userList);
        Map<Integer, String> userMap = userInfoList.stream()
                .collect(Collectors.toMap(UserInfo::getId, UserInfo::getNickName));
        // 获取审核员审核
        List<DictVO> dict = sysDictApiExp.getDict(TypeCode.PUBLICITY.value(),
                DictCode.PUBLICITY_APPLICATION_DETAIL_STATE.value());
        Map<String, String> dictMap = dict.stream().collect(Collectors.toMap(DictVO::getValue, DictVO::getLabel));
        String batchNum = getBatchNum(goodsReqVO);
        if (batchNum == null) {
            return new ArrayList<>();
        }
        // 查询该公司是否申领
        List<ApplicationByUnitVO> applicationByUnitVOList = applicationMapper.selectByUnit(goodsReqVO.getUnitId(), batchNum);
        // 得到所有公司下所有货物（goodsId）
        Map<Integer, List<ApplicationByUnitVO>> map = applicationByUnitVOList.stream()
                .collect(Collectors.groupingBy(ApplicationByUnitVO::getUnitId));
        // 得到该货物的物料审核员和审核状态
        List<UnitGoodsStateVO> unitGoodsStateVOS = new ArrayList<>();
        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        List<Integer> unitIdList = unitApiExp.getAllUnitId();
        unitIdList.add(11);
        // 得到各单位的审核状态
        for (Integer unitId : unitIdList) {
            List<ApplicationByUnitVO> list = map.get(unitId);
            // 如果该单位能够申领分公司却未提交
            if (list != null && list.get(0).getState() == 0) {
                unitGoodsStateVOS.add(UnitGoodsStateVO.builder()
                        .unitName(unitMap.get(unitId))
                        .state(dictMap.get(String.valueOf(0))).build());
            }
            // 获取该单位下的所有审核人的审核状况
            else if (list != null) {
                UnitGoodsStateVO vo = new UnitGoodsStateVO();
                vo.setUnitName(unitMap.get(unitId));
                StringBuilder state = new StringBuilder();
                Map<String, Integer> checkerMap = new HashMap<>();
                // 判断state是否有拒绝
                // 判断state是否有未审批
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
        // 通过单位id得到该分公司下的所有单位和状态
        return unitGoodsStateVOS;
    }

    @Override
    public List<SummaryUnitGoodsVO> getUnitSummaryList(GoodsReqVO goodsReqVO, Integer type) {
        String batchNum = getBatchNum(goodsReqVO);
        // 展示该次该单位下的申请汇总。
        return applicationDetailMapper.selectSummaryUnitByType(SummaryCondVO.builder()
                .batchNum(batchNum)
                .unitId(goodsReqVO.getUnitId())
                .type(type)
                .build());
    }

    @Override
    public List<VerifyGoodsVO> getList(Integer userId) {
        Date date = new Date();
        // 通过此刻时间与截止时间对比，判断为第几月第几次
        GoodsSetting setting = goodsSettingService.getSetByDate(DateUtil.dateToString(date));
        if (setting == null) {
            throw new PublicityException(PublicityErrorCode.NOT_NUMBER_IN_MONTH);
        }
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(setting.getDeadline()), "yyyyMM") + setting.getGoodsIndex();
        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        // 通过时间 ，每次获取其列表
        List<VerifyGoodsVO> verifyGoodsVOS = goodsMapper.selectVerifyList(userId, batchNum);
        for (VerifyGoodsVO verifyGoodsVO : verifyGoodsVOS) {
            verifyGoodsVO.setUnitName(unitMap.get(verifyGoodsVO.getUnitId()));
        }
        return verifyGoodsVOS;
    }

    @Override
    public List<VerifyHallGoodsVO> getHallList(GoodsCheckerVO goodsCheckerVO) {
        Date date = new Date();
        // 通过此刻时间与截止时间对比，判断为第几月第几次
        GoodsSetting setting = goodsSettingService.getSetByDate(DateUtil.dateToString(date));
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(setting.getDeadline()), "yyyyMM") + setting.getGoodsIndex();
        List<VerifyHallGoodsVO> list = goodsMapper.selectVerifyHallList(VerifyHallGoodsReqVO.builder()
                .goodsId(goodsCheckerVO.getGoodsId())
                .unitId(goodsCheckerVO.getUnitId())
                .batchNum(batchNum)
                .build()
        );
        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        // 得到该单位下所有营业厅map
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
        // 通过此刻时间与截止时间对比，判断为第几月第几次
        GoodsSetting setting = goodsSettingService.getSetByDate(DateUtil.dateToString(date));
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(setting.getDeadline()), "yyyyMM") + setting.getGoodsIndex();
        // 得到该批该次审核的申报
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
        // 通过物料批量修改审核状态
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
            // 通过时间判断批次
            goodsSetting = goodsSettingService.getSetByDate(DateUtil.dateToString(new Date()));
        } else {
            goodsSetting = goodsSettingService.getByCond(goodsApproveVO.getTime(), goodsApproveVO.getGoodsIndex());
        }
        goodsApproveVO.setTime(goodsSetting.getDeadline());
        if (goodsApproveVO.getGoodsIndex() == null) {
            goodsApproveVO.setGoodsIndex(goodsSetting.getGoodsIndex());
        }
        // 获取登入用户单位
        UserInfo user = sysUserApiExp.getUserInfoById(userId);
        //  2.获取流程id 通过流程状态活动流程id
        List<Flow> flows = flowApiExp.getFlowsByTypeId(goodsApproveVO.getFlowTypeId());
        if (flows == null || flows.size() != 1) {
            throw new PublicityException(PublicityErrorCode.NOT_HAVE_FLOW);
        }
        Flow flow = flows.get(0);
        List<FlowNodePropVO> flowProps = flowNodePropApiExp.getNodeProps(flow.getId());
        //  3.校验用户发起审批权限
        boolean hasAccess = hasAccess2Approve(flowProps, userId, user.getUnitId());
        if (!hasAccess) {
            throw new PublicityException(PublicityErrorCode.NOT_POWER_TO_APPROVER);
        }
        // 通过时间和次序得到该次下的所有申请id（已提交且未被拒绝）
        GoodsSetting setInfo = goodsSettingService.getSetByDate(goodsApproveVO.getTime());
        // 获取批次号
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(setInfo.getDeadline()), "yyyyMM")
                + setInfo.getGoodsIndex();
        //  4.同步节点属性
        syncAppliactionFlow(flowProps, batchNum, userId, user.getUnitId());
        // 得到推送模板
        String inform = getInform(flowProps.get(0).getFlowNodeId()
                , FlowNodeNoticeState.DEFAULT_REMINDER.value());
        if (inform == null) {
            return;
        }
        // 修改推送模板
        inform = inform.replace("title"
                , "宣传用品" + batchNum + "批次");
        // 推送消息给发起人
        applicationNoticeService.saveApplicationNotice(
                ApplicationNoticeResVO.builder()
                        .batchNum(batchNum)
                        .receiver(userId)
                        .promoter(userId)
                        .content(inform)
                        .flowTypeId(goodsApproveVO.getFlowTypeId())
                        .state(0)
                        .build());
        // 修改申领审批状态
        List<Application> applicationList = applicationMapper.createLambdaQuery()
                .andEq(Application::getBatchNum, batchNum).select();
        List<Long> applicationIdList = new ArrayList<>();
        for (Application application : applicationList) {
            applicationIdList.add(application.getId());
        }
        List<Integer> stateList = new ArrayList<>();
        stateList.add(1);
        stateList.add(2);
        // 该批次下归属于该单位的物料申请审批状态修改,待审核都变为同意
        applicationDetailMapper.createLambdaQuery()
                .andIn(ApplicationDetail::getApplicationId, applicationIdList)
                .andEq(ApplicationDetail::getUnderUnitId, goodsApproveVO.getUnderUnitId())
                .andIn(ApplicationDetail::getState, stateList)
                .updateSelective(ApplicationDetail.builder()
                        .approvedState(NodeState.APPROVING.value())
                        .state(2)
                        .build());
        // 修改批次状态
        goodsSettingService.updateByBatchNum(batchNum);
        // 修改审核状态
        verifyNoticeMapper.createLambdaQuery()
                .andEq(VerifyNotice::getBatchNum, batchNum)
                .andEq(VerifyNotice::getUnitId, goodsApproveVO.getUnderUnitId())
                .updateSelective(VerifyNotice.builder().state(1).build());
    }

    @Override
    public void approve(ApplicationApproveVO approveVO, Integer userId) {
        // 查询当前节点信息
        ApplicationFlow currentFlow = applicationFlowService
                .getInfoById(approveVO.getId());
        // 校验审批人是否为本人
        if (!currentFlow.getUserId().equals(userId)) {
            throw new PublicityException(PublicityErrorCode.LOCK_OF_APPROVAL_ROLE);
        }
        // 项目签报批次号
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
                        throw new PublicityException(PublicityErrorCode.NOT_RELEVANT_FLOW);
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
                        throw new PublicityException(PublicityErrorCode.NOT_RELEVANT_FLOW);
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
        applicationNoticeService.updateByBatchNum(batchNum, approveVO.getUnderUnitId());

        // 推送提醒
        assert operation != null;
        toInform(operation, approvers, userId, batchNum, currentFlowNodeId, flowNodes,
                approverMap, approveVO.getSuggestion(), approveVO.getUnderUnitId());

        // 更新项目节点信息
        applicationFlowService.updateById(ApplicationFlow.builder()
                .operation(operation)
                .suggestion(approveVO.getSuggestion())
                .updateTime(DateUtil.dateToString(new Date()))
                .id(approveVO.getId())
                .batchNum(batchNum)
                .build());

        // 更新项目的流程状态
        if (projectState != null) {
            // 得到申领id
            List<Application> applicationList = applicationMapper.createLambdaQuery()
                    .andEq(Application::getBatchNum, batchNum).select();
            List<Long> applicationIdList = new ArrayList<>();
            for (Application application : applicationList) {
                applicationIdList.add(application.getId());
            }
            // 该批次下归属于该单位的物料申请审批状态修改
            applicationDetailMapper.createLambdaQuery()
                    .andIn(ApplicationDetail::getApplicationId, applicationIdList)
                    .andEq(ApplicationDetail::getUnderUnitId, approveVO.getUnderUnitId())
                    .updateSelective(ApplicationDetail.builder()
                            .approvedState(projectState)
                            .build());
        }
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
        return flowNoticeApiExp.getInform(flowNodeId, state);
    }

    /**
     * 同步节点属性
     */
    private void syncAppliactionFlow(List<FlowNodePropVO> flowProps, String batchNum, Integer userId, Integer unitId) {

        // 用来存储同步节点的list
        List<ApplicationFlow> list = new ArrayList<>();
        // 判断节点是否有保存属性
        for (FlowNodePropVO flowPropVO : flowProps) {
            if (flowPropVO.getIsJoin() == null || flowPropVO.getControlAccess() == null) {
                throw new PublicityException(PublicityErrorCode.NOT_RELEVANT_FLOW);
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
                    .unitId(unitId)
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
                          Map<String, Integer> approverMap, String suggestion, Integer underUnitId) {
        // 通过flowNodeId得到流程类型id
        Long flowTypeId = flowTypeApiExp.getTypeIdByNode(flowNodes.get(0));
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
                        ApplicationNoticeResVO.builder().batchNum(batchNum)
                                .receiver(nextApprover)
                                .promoter(userId)
                                .content(inform)
                                .flowTypeId(flowTypeId)
                                .state(0)
                                .underUnitId(underUnitId)
                                .build());
                // 推送邮件
                if (mailEnable) {
                    // 下一节点审批人信息
                    UserInfo nextApproverInfo = sysUserApiExp.getUserInfoById(nextApprover);
                    // 推送内容
                    String info = "宣传用品" + batchNum + "费用签报";
                    mailService.postMail(nextApproverInfo.getEmail(), nextApproverInfo.getNickName(), info);
                }
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
                    ApplicationNoticeResVO.builder().batchNum(batchNum)
                            .receiver(approvers.get(0).getUserId())
                            .promoter(userId)
                            .content(inform)
                            .flowTypeId(flowTypeId)
                            .underUnitId(underUnitId)
                            .state(0)
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
     * 获取汇总
     */
    private List<SummaryUnitApplicationVO> getApplicationSum(String batchNum, Integer unitId, Integer type) {
        // 展示该次该单位下的申请汇总。
        return applicationDetailMapper.selectApplicationSumByType(SummaryCondVO.builder()
                .batchNum(batchNum)
                .unitId(unitId)
                .type(type)
                .build());
    }

    private String getBatchNum(GoodsReqVO goodsReqVO) {
        Integer hangzhou = UnitEnum.HANGZHOU.value();
        // 如果为杭州则能看全部
        if (hangzhou.equals(goodsReqVO.getUnitId())) {
            goodsReqVO.setUnitId(null);
        }
        GoodsSetting goodsSetting = null;
        if (goodsReqVO.getTime() != null && goodsReqVO.getGoodsIndex() == null) {
            return null;
        }
        if (goodsReqVO.getTime() == null) {
            // 通过时间判断批次
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
