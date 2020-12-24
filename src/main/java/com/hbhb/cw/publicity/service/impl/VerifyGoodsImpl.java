package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.bean.BeanConverter;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.publicity.enums.GoodsType;
import com.hbhb.cw.publicity.enums.PublicityErrorCode;
import com.hbhb.cw.publicity.exception.PublicityException;
import com.hbhb.cw.publicity.mapper.ApplicationDetailMapper;
import com.hbhb.cw.publicity.mapper.ApplicationMapper;
import com.hbhb.cw.publicity.mapper.GoodsMapper;
import com.hbhb.cw.publicity.model.Application;
import com.hbhb.cw.publicity.model.ApplicationDetail;
import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.cw.publicity.rpc.UnitApiExp;
import com.hbhb.cw.publicity.service.GoodsSettingService;
import com.hbhb.cw.publicity.service.VerifyGoodsService;
import com.hbhb.cw.publicity.web.vo.GoodsChangerVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.SummaryCondVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsVO;
import com.hbhb.cw.publicity.web.vo.VerifyGoodsExportVO;

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
    private UnitApiExp unitApiExp;

    @Override
    public SummaryGoodsResVO getAuditList(GoodsReqVO goodsReqVO) {
//        state = state == null ? 0 : state;
        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        List<SummaryGoodsVO> simList = getSummaryList(goodsReqVO, GoodsType.BUSINESS_SIMPLEX.getValue());
        for (int i = 0; i < simList.size(); i++) {
            simList.get(i).setLineNum(i + 1L);
            simList.get(i).setUnitName(unitMap.get(simList.get(i).getUnitId()));
            simList.get(i).setHallName(simList.get(i).getHallId().toString());
        }
        List<SummaryGoodsVO> singList = getSummaryList(goodsReqVO, GoodsType.FLYER_PAGE.getValue());
        for (int i = 0; i < singList.size(); i++) {
            singList.get(i).setLineNum(i + 1L);
            singList.get(i).setUnitName(unitMap.get(singList.get(i).getUnitId()));
            singList.get(i).setHallName(singList.get(i).getHallId().toString());
        }
        return new SummaryGoodsResVO(simList, singList, getFlag(goodsReqVO), getCheckerState(goodsReqVO));
    }

    @Override
    public void  saveGoods(GoodsReqVO goodsReqVO) {
        goodsReqVO.setHallId(null);
        List<Long> applicationIds = getApplicationIds(goodsReqVO);
        applicationMapper.createLambdaQuery()
                .andIn(Application::getId, applicationIds)
                .updateSelective(Application.builder().editable(true).build());
    }

    @Override
    public void submitGoods(GoodsReqVO goodsReqVO) {
        goodsReqVO.setHallId(null);
        // 当提交时提交为整个分公司
        List<Long> applicationIds = getApplicationIds(goodsReqVO);
        List<ApplicationDetail> applicationDetailList = applicationDetailMapper
                .createLambdaQuery()
                .andIn(ApplicationDetail::getApplicationId, applicationIds)
                .select();
        List<Long> detailIds = new ArrayList<>();
        for (ApplicationDetail applicationDetail : applicationDetailList) {
            detailIds.add(applicationDetail.getId());
        }
        List<Integer> stateList = new ArrayList<>();
        stateList.add(0);
        stateList.add(3);
        // 判断为第一次提交还是，被拒绝后提交
        // 提交
        applicationMapper.createLambdaQuery()
                .andIn(Application::getId, applicationIds)
                .updateSelective(Application.builder().submit(true).editable(true).build());
        applicationDetailMapper.createLambdaQuery()
                .andIn(ApplicationDetail::getId, detailIds)
                .andIn(ApplicationDetail::getState, stateList)
                .updateSelective(ApplicationDetail.builder().state(1).build());
        // 判断工作台有没有 ，没有则发送工作台

    }

    @Override
    public void changerModifyAmount(List<GoodsChangerVO> list) {
        List<Long> applicationDetailIds = new ArrayList<>();
        // id => modifyAmount id => 申请数量
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
        // 批量调试修改后申请数量
        applicationDetailMapper.updateBatchTempById(applicationDetailList);
    }

    @Override
    public List<List<VerifyGoodsExportVO>> getExportList(GoodsReqVO goodsReqVO) {
        SummaryGoodsResVO auditList = getAuditList(goodsReqVO);
        // 得到业务单式
        List<SummaryGoodsVO> simplexList = auditList.getSimplexList();
        // 得到宣传单页
        List<SummaryGoodsVO> singleList = auditList.getSingleList();
        List<List<VerifyGoodsExportVO>> list = new ArrayList<>();
        list.add(BeanConverter.copyBeanList(simplexList, VerifyGoodsExportVO.class));
        list.add(BeanConverter.copyBeanList(singleList, VerifyGoodsExportVO.class));
        return list;
    }

    /**
     * 获取需要修改的申领id
     */
    private List<Long> getApplicationIds(GoodsReqVO goodsReqVO) {
        GoodsSetting goodsSetting = null;
        if (goodsReqVO.getTime() != null && goodsReqVO.getGoodsIndex() == null) {
            return new ArrayList<>();
        }
        if (goodsReqVO.getTime() == null) {
            // 通过时间判断批次
            goodsSetting = goodsSettingService.getSetByDate(DateUtil.dateToString(new Date()));
            if (goodsSetting == null) {
                return new ArrayList<>();
            }
            goodsReqVO.setTime(goodsSetting.getDeadline());
        } else {
            goodsSetting = goodsSettingService.getByCond(goodsReqVO.getTime(), goodsReqVO.getGoodsIndex());
            if (goodsSetting == null) {
                return new ArrayList<>();
            }
            goodsReqVO.setTime(goodsSetting.getDeadline());
        }
        if (goodsReqVO.getGoodsIndex() == null) {
            goodsReqVO.setGoodsIndex(goodsSetting.getGoodsIndex());
        }
        // 得到第几次，判断此次是否结束。
        if (goodsSetting.getIsEnd() != null
                || DateUtil.stringToDate(goodsSetting.getDeadline()).getTime() < DateUtil.stringToDate(goodsReqVO.getTime()).getTime()) {
            // 报异常
            throw new PublicityException(PublicityErrorCode.ALREADY_CLOSE);
        }
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(goodsReqVO.getTime()), "yyyyMM") + goodsReqVO.getGoodsIndex();

        // 展示该次该单位下的申请汇总。
        return goodsMapper.selectIdsByCond(SummaryCondVO.builder()
                .batchNum(batchNum)
                .unitId(goodsReqVO.getUnitId())
                .hallId(goodsReqVO.getHallId())
                .build());
    }


    /**
     * 获取分公司汇总（审核）
     */
    private List<SummaryGoodsVO> getSummaryList(GoodsReqVO goodsReqVO, Integer type) {
        GoodsSetting goodsSetting = null;
        if (goodsReqVO.getTime() != null && goodsReqVO.getGoodsIndex() == null) {
            return new ArrayList<>();
        }
        if (goodsReqVO.getTime() == null) {
            // 通过时间判断批次
            goodsSetting = goodsSettingService.getSetByDate(DateUtil.dateToString(new Date()));
            if (goodsSetting == null) {
                return new ArrayList<SummaryGoodsVO>();
            }
            goodsReqVO.setTime(goodsSetting.getDeadline());
        }
        else {
            goodsSetting = goodsSettingService.getByCond(goodsReqVO.getTime(), goodsReqVO.getGoodsIndex());
            if (goodsSetting == null) {
                return new ArrayList<SummaryGoodsVO>();
            }
            goodsReqVO.setTime(goodsSetting.getDeadline());
        }
        if (goodsReqVO.getGoodsIndex() == null) {
            goodsReqVO.setGoodsIndex(goodsSetting.getGoodsIndex());
        }
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(goodsReqVO.getTime()), "yyyyMM") + goodsReqVO.getGoodsIndex();

        // 展示该次该单位下的申请汇总。
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
     * 判断是否提交
     */
    private Boolean getFlag(GoodsReqVO goodsReqVO) {
        GoodsSetting goodsSetting = null;
        if (goodsReqVO.getTime() != null && goodsReqVO.getGoodsIndex() == null) {
            return false;
        }
        if (goodsReqVO.getTime() == null) {
            // 通过时间判断批次
            goodsSetting = goodsSettingService.getSetByDate(DateUtil.dateToString(new Date()));
            if (goodsSetting == null || goodsSetting.getDeadline()==null) {
                return false;
            }
            goodsReqVO.setTime(goodsSetting.getDeadline());
        }
        else {
            goodsSetting = goodsSettingService.getByCond(goodsReqVO.getTime(), goodsReqVO.getGoodsIndex());
            if (goodsSetting == null || goodsSetting.getDeadline()==null) {
                return false;
            }
            goodsReqVO.setTime(goodsSetting.getDeadline());
        }
        if (goodsReqVO.getGoodsIndex() == null) {
            goodsReqVO.setGoodsIndex(goodsSetting.getGoodsIndex());
        }
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(goodsSetting.getDeadline()), "yyyyMM") + goodsReqVO.getGoodsIndex();

        // 得到第几次，判断此次是否结束。如果结束提交置灰
        if (goodsSetting.getIsEnd() != null
                || DateUtil.stringToDate(goodsSetting.getDeadline()).getTime() < DateUtil.stringToDate(goodsReqVO.getTime()).getTime()) {
            // 如果结束提交置灰
            return false;
        }
        // 展示该次该单位下的申请汇总。
        List<SummaryGoodsVO> summaries = goodsMapper.selectSummaryByState(SummaryCondVO.builder()
                .batchNum(batchNum)
                .unitId(goodsReqVO.getUnitId())
                .state(goodsReqVO.getDetailState())
                .hallId(goodsReqVO.getHallId())
                .build());
        // 判断此次是否有过提交,如果已提交，提交置灰
        // 通过单位和时间次序得到所有该次该单位下所有申请表
        for (SummaryGoodsVO summary : summaries) {
            if (summary.getState() == 0 || summary.getState() == 3) {
                return true;
            }
        }
        return false;
    }

    /**
     * 物料确认状态
     */
    private String getCheckerState(GoodsReqVO goodsReqVO) {
        String checkerState = "已审核";
        GoodsSetting goodsSetting = null;
        if (goodsReqVO.getTime() != null && goodsReqVO.getGoodsIndex() == null) {
            return new String();
        }
        if (goodsReqVO.getTime() == null) {
            // 通过时间判断批次
            goodsSetting = goodsSettingService.getSetByDate(DateUtil.dateToString(new Date()));
            goodsReqVO.setTime(goodsSetting.getDeadline());
        }
        else {
            goodsSetting = goodsSettingService.getByCond(goodsReqVO.getTime(), goodsReqVO.getGoodsIndex());
        }
        if (goodsSetting == null) {
            return checkerState;
        }
        if (goodsReqVO.getGoodsIndex() == null) {
            goodsReqVO.setGoodsIndex(goodsSetting.getGoodsIndex());
        }
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(goodsReqVO.getTime()), "yyyyMM") + goodsReqVO.getGoodsIndex();

        // 展示该次该单位下的申请汇总。
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
            return "归属部门审核未通过，请分公司修改";
        } else if (states.contains(0)) {
            return "分公司没有提交，请分公司提交";
        } else if (states.contains(1)) {
            return "分公司已提交等待归属部门审核";
        } else {
            return "归属部门已确认";
        }
    }
}
