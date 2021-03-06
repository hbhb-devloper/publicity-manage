package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.bean.BeanConverter;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.publicity.enums.GoodsType;
import com.hbhb.cw.publicity.mapper.ApplicationDetailMapper;
import com.hbhb.cw.publicity.mapper.ApplicationMapper;
import com.hbhb.cw.publicity.mapper.GoodsMapper;
import com.hbhb.cw.publicity.mapper.VerifyNoticeMapper;
import com.hbhb.cw.publicity.model.Application;
import com.hbhb.cw.publicity.model.ApplicationDetail;
import com.hbhb.cw.publicity.model.Goods;
import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.cw.publicity.model.VerifyNotice;
import com.hbhb.cw.publicity.rpc.HallApiExp;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.rpc.UnitApiExp;
import com.hbhb.cw.publicity.service.GoodsSettingService;
import com.hbhb.cw.publicity.service.VerifyGoodsService;
import com.hbhb.cw.publicity.web.vo.GoodsChangerVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.GoodsSaveGoodsVO;
import com.hbhb.cw.publicity.web.vo.SummaryCondVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsVO;
import com.hbhb.cw.publicity.web.vo.VerifyGoodsExportVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-12-14
 */
@Slf4j
@Service
public class VerifyGoodsImpl implements VerifyGoodsService {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private ApplicationMapper applicationMapper;
    @Resource
    private GoodsSettingService goodsSettingService;
    @Resource
    private ApplicationDetailMapper applicationDetailMapper;
    @Resource
    private VerifyNoticeMapper verifyNoticeMapper;
    @Resource
    private UnitApiExp unitApiExp;
    @Resource
    private SysUserApiExp sysUserApiExp;
    @Resource
    private HallApiExp hallApiExp;

    @Override
    public SummaryGoodsResVO getAuditList(GoodsReqVO goodsReqVO) {
        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        String time = goodsReqVO.getTime();
        // ?????????????????????????????????map
        Map<Integer, String> map = hallApiExp.selectHallByUnitId(goodsReqVO.getUnitId());
        if (map == null || map.keySet().size() == 0) {
            return new SummaryGoodsResVO();
        }
        String batchNum = getBatchNum(goodsReqVO);
        List<SummaryGoodsVO> simList = getSummaryList(batchNum, goodsReqVO, GoodsType.BUSINESS_SIMPLEX.getValue());
        for (int i = 0; i < simList.size(); i++) {
            simList.get(i).setLineNum(i + 1L);
            simList.get(i).setUnitName(unitMap.get(simList.get(i).getUnitId()));
            simList.get(i).setHallName(map.get(Math.toIntExact(simList.get(i).getHallId())));
        }
        List<SummaryGoodsVO> singList = getSummaryList(batchNum, goodsReqVO, GoodsType.FLYER_PAGE.getValue());
        for (int i = 0; i < singList.size(); i++) {
            singList.get(i).setLineNum(i + 1L);
            singList.get(i).setUnitName(unitMap.get(singList.get(i).getUnitId()));
            singList.get(i).setHallName(map.get(Math.toIntExact(singList.get(i).getHallId())));
        }
        goodsReqVO.setTime(time);
        return new SummaryGoodsResVO(simList, singList, getFlag(goodsReqVO), getCheckerState(goodsReqVO));
    }

    @Override
    public void saveGoods(GoodsSaveGoodsVO goodsSaveGoodsVO) {
        // ??????????????????
        List<GoodsChangerVO> list = goodsSaveGoodsVO.getList();
        changerModifyAmount(list);
        // ??????
        GoodsReqVO goodsReqVO = goodsSaveGoodsVO.getGoodsReqVO();
        goodsReqVO.setHallId(null);
        String batchNum = getBatchNum(goodsReqVO);
        List<Long> applicationIds = getApplicationIds(goodsReqVO, batchNum);
        applicationMapper.createLambdaQuery()
                .andIn(Application::getId, applicationIds)
                .updateSelective(Application.builder().editable(true).build());
    }

    @Override
    public void submitGoods(GoodsSaveGoodsVO goodsSaveGoodsVO) {
        // ??????????????????
        saveGoods(goodsSaveGoodsVO);
        // ??????
        GoodsReqVO goodsReqVO = goodsSaveGoodsVO.getGoodsReqVO();
        goodsReqVO.setHallId(null);
        String time = goodsSaveGoodsVO.getGoodsReqVO().getTime();
        goodsReqVO.setTime(DateUtil.dateToString(DateUtil.stringToDate(time), "yyyy-MM"));
        // ????????????????????????????????????
        String batchNum = getBatchNum(goodsReqVO);
        List<Long> applicationIds = getApplicationIds(goodsReqVO, batchNum);
        List<ApplicationDetail> applicationDetailList = applicationDetailMapper
                .createLambdaQuery()
                .andIn(ApplicationDetail::getApplicationId, applicationIds)
                .select();
        List<Long> detailIds = new ArrayList<>();
        for (ApplicationDetail applicationDetail : applicationDetailList) {
            detailIds.add(applicationDetail.getId());
        }
        List<Integer> stateList = new ArrayList<>();
        // ??????????????????????????????
        stateList.add(0);
        stateList.add(3);
        // ???????????????????????????????????????????????????
        // ??????
        applicationMapper.createLambdaQuery()
                .andIn(Application::getId, applicationIds)
                .updateSelective(Application.builder().submit(true).editable(true).build());
        applicationDetailMapper.createLambdaQuery()
                .andIn(ApplicationDetail::getId, detailIds)
                .andIn(ApplicationDetail::getState, stateList)
                .updateSelective(ApplicationDetail.builder().state(1).build());
        // ????????????
        // ??????????????????????????????????????????
        List<Long> goodsIdList = new ArrayList<>();
        List<GoodsChangerVO> list = goodsSaveGoodsVO.getList();
        for (GoodsChangerVO goodsChangerVO : list) {
            goodsIdList.add(goodsChangerVO.getGoodsId());
        }
        // ????????????id?????????????????????????????????
        List<Goods> goodsList = goodsMapper.createLambdaQuery().andIn(Goods::getId, goodsIdList).select();
        List<Integer> userIdList = new ArrayList<>();
        // ??????????????????????????????
        for (Goods goods : goodsList) {
            userIdList.add(goods.getChecker());
        }
        // ?????????????????????????????????
        List<VerifyNotice> notices = verifyNoticeMapper.createLambdaQuery()
                .andEq(VerifyNotice::getBatchNum, batchNum)
                .andEq(VerifyNotice::getState, 0)
                .select();
        // ???????????????????????????????????????
        List<Integer> receiverList = new ArrayList<>();
        for (VerifyNotice notice : notices) {
            receiverList.add(notice.getReceiver());
        }
        userIdList.removeAll(receiverList);
        // ??????????????????
        if (userIdList.size() != 0) {
            List<Integer> userIds = new ArrayList<>();
            for (Integer userId : userIdList) {
                if (userIds.contains(userId)){
                    continue;
                }
                userIds.add(userId);
                UserInfo user = sysUserApiExp.getUserInfoById(userId);
                verifyNoticeMapper.insertTemplate(
                        VerifyNotice.builder()
                                .batchNum(batchNum)
                                .content("?????????????????????????????????  ????????????" + batchNum)
                                .createTime(new Date())
                                .receiver(userId)
                                .unitId(user.getUnitId())
                                .state(0)
                                .build()
                );
            }
        }
    }


    private void changerModifyAmount(List<GoodsChangerVO> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        List<Long> applicationDetailIds = new ArrayList<>();
        // id => modifyAmount id => ????????????
        Map<Long, Long> amountMap = new HashMap<>();
        for (GoodsChangerVO goodsChangerVO : list) {
            applicationDetailIds.add(goodsChangerVO.getId());
            amountMap.put(goodsChangerVO.getId(), goodsChangerVO.getModifyAmount());
        }
        List<ApplicationDetail> applicationDetailList = applicationDetailMapper
                .createLambdaQuery().andIn(ApplicationDetail::getId, applicationDetailIds).select();
        for (ApplicationDetail applicationDetail : applicationDetailList) {
            applicationDetail.setModifyAmount(amountMap.get(applicationDetail.getId()));
        }
        // ?????????????????????????????????
        applicationDetailMapper.updateBatchTempById(applicationDetailList);
    }

    @Override
    public List<List<VerifyGoodsExportVO>> getExportList(GoodsReqVO goodsReqVO) {
        SummaryGoodsResVO auditList = getAuditList(goodsReqVO);
        // ??????????????????
        List<SummaryGoodsVO> simplexList = auditList.getSimplexList();
        // ??????????????????
        List<SummaryGoodsVO> singleList = auditList.getSingleList();
        List<List<VerifyGoodsExportVO>> list = new ArrayList<>();
        list.add(BeanConverter.copyBeanList(simplexList, VerifyGoodsExportVO.class));
        list.add(BeanConverter.copyBeanList(singleList, VerifyGoodsExportVO.class));
        return list;
    }

    /**
     * ???????????????????????????id
     */
    private List<Long> getApplicationIds(GoodsReqVO goodsReqVO, String batchNum) {
        // ??????????????????????????????????????????
        return goodsMapper.selectIdsByCond(SummaryCondVO.builder()
                .batchNum(batchNum)
                .unitId(goodsReqVO.getUnitId())
                .hallId(goodsReqVO.getHallId())
                .build());
    }

    private String getBatchNum(GoodsReqVO goodsReqVO) {
        GoodsSetting goodsSetting = null;
        if (goodsReqVO.getTime() != null && goodsReqVO.getGoodsIndex() == null) {
            return "";
        }
        if (goodsReqVO.getTime() == null) {
            // ????????????????????????
            goodsSetting = goodsSettingService.getSetByDate(DateUtil.dateToString(new Date()));
        } else {
            goodsSetting = goodsSettingService.getByCond(goodsReqVO.getTime(), goodsReqVO.getGoodsIndex());
        }
        if (goodsSetting == null) {
            return "";
        }
        goodsReqVO.setTime(goodsSetting.getDeadline());
        if (goodsReqVO.getGoodsIndex() == null) {
            goodsReqVO.setGoodsIndex(goodsSetting.getGoodsIndex());
        }
        return DateUtil.dateToString(DateUtil.stringToDate(goodsReqVO.getTime()), "yyyyMM") + goodsReqVO.getGoodsIndex();

    }

    /**
     * ?????????????????????????????????
     */
    private List<SummaryGoodsVO> getSummaryList(String batchNum, GoodsReqVO goodsReqVO, Integer type) {
        // ??????????????????????????????????????????
        List<SummaryGoodsVO> summaries = goodsMapper.selectSummaryByState(SummaryCondVO.builder()
                .batchNum(batchNum)
                .unitId(goodsReqVO.getUnitId())
                .type(type)
                .state(goodsReqVO.getDetailState())
                .hallId(goodsReqVO.getHallId())
                .build());
        for (int i = summaries.size() - 1; i >= 0; i--) {
            if (summaries.get(i).getApplyAmount() == null) {
                summaries.remove(i);
            }
        }
        return summaries;
    }

    /**
     * ??????????????????
     */
    private Boolean getFlag(GoodsReqVO goodsReqVO) {
        GoodsSetting goodsSetting = null;
        if (goodsReqVO.getTime() != null && goodsReqVO.getGoodsIndex() == null) {
            return false;
        }
        if (goodsReqVO.getTime() == null) {
            // ????????????????????????
            goodsSetting = goodsSettingService.getSetByDate(DateUtil.dateToString(new Date()));
            if (goodsSetting == null || goodsSetting.getDeadline() == null) {
                return false;
            }
            goodsReqVO.setTime(goodsSetting.getDeadline());
        } else {
            goodsSetting = goodsSettingService.getByCond(goodsReqVO.getTime(), goodsReqVO.getGoodsIndex());
            if (goodsSetting == null || goodsSetting.getDeadline() == null) {
                return false;
            }
            goodsReqVO.setTime(goodsSetting.getDeadline());
        }
        if (goodsReqVO.getGoodsIndex() == null) {
            goodsReqVO.setGoodsIndex(goodsSetting.getGoodsIndex());
        }
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(goodsSetting.getDeadline()), "yyyyMM") + goodsReqVO.getGoodsIndex();

        // ?????????????????????????????????????????????????????????????????????
        if (goodsSetting.getIsEnd() != null
                || DateUtil.stringToDate(goodsSetting.getDeadline()).getTime() < DateUtil.stringToDate(goodsReqVO.getTime()).getTime()) {
            // ????????????????????????
            return false;
        }
        // ???????????????????????????????????????
        List<Application> flowList = applicationMapper.createLambdaQuery().andEq(Application::getBatchNum, batchNum).select();
        if (flowList != null && flowList.size() != 0) {
            return false;
        }
        // ??????????????????????????????????????????
        List<SummaryGoodsVO> summaries = goodsMapper.selectSummaryByState(SummaryCondVO.builder()
                .batchNum(batchNum)
                .unitId(goodsReqVO.getUnitId())
                .state(goodsReqVO.getDetailState())
                .hallId(goodsReqVO.getHallId())
                .build());
        // ??????????????????????????????,??????????????????????????????
        // ????????????????????????????????????????????????????????????????????????
        for (SummaryGoodsVO summary : summaries) {
            if (summary.getState() == 0 || summary.getState() == 3) {
                return true;
            }
        }
        return false;
    }

    /**
     * ??????????????????
     */
    private String getCheckerState(GoodsReqVO goodsReqVO) {
        String checkerState = "?????????";
        GoodsSetting goodsSetting = null;
        if (goodsReqVO.getTime() != null && goodsReqVO.getGoodsIndex() == null) {
            return new String();
        }
        if (goodsReqVO.getTime() == null) {
            // ????????????????????????
            goodsSetting = goodsSettingService.getSetByDate(DateUtil.dateToString(new Date()));
            goodsReqVO.setTime(goodsSetting.getDeadline());
        } else {
            goodsSetting = goodsSettingService.getByCond(goodsReqVO.getTime(), goodsReqVO.getGoodsIndex());
        }
        if (goodsSetting == null) {
            return checkerState;
        }
        if (goodsReqVO.getGoodsIndex() == null) {
            goodsReqVO.setGoodsIndex(goodsSetting.getGoodsIndex());
        }
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(goodsReqVO.getTime()), "yyyyMM") + goodsReqVO.getGoodsIndex();

        // ??????????????????????????????????????????
        List<SummaryGoodsVO> summaries = goodsMapper.selectSummaryByState(SummaryCondVO.builder()
                .batchNum(batchNum)
                .unitId(goodsReqVO.getUnitId())
                .state(goodsReqVO.getDetailState())
                .hallId(goodsReqVO.getHallId())
                .build());
        List<Integer> states = new ArrayList<>();
        for (SummaryGoodsVO summary : summaries) {
            states.add(summary.getState());
        }
        if (states.contains(3)) {
            return "????????????????????????????????????????????????";
        } else if (states.contains(0)) {
            return "??????????????????????????????????????????";
        } else if (states.contains(1)) {
            return "??????????????????????????????????????????";
        } else {
            return "?????????????????????";
        }
    }
}
