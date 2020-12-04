package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.publicity.enums.GoodsErrorCode;
import com.hbhb.cw.publicity.enums.GoodsType;
import com.hbhb.cw.publicity.exception.GoodsException;
import com.hbhb.cw.publicity.mapper.ApplicationDetailMapper;
import com.hbhb.cw.publicity.mapper.ApplicationMapper;
import com.hbhb.cw.publicity.mapper.GoodsMapper;
import com.hbhb.cw.publicity.model.Application;
import com.hbhb.cw.publicity.model.ApplicationDetail;
import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.service.GoodsService;
import com.hbhb.cw.publicity.service.GoodsSettingService;
import com.hbhb.cw.publicity.web.vo.ApplicationVO;
import com.hbhb.cw.publicity.web.vo.GoodsChangerVO;
import com.hbhb.cw.publicity.web.vo.GoodsCondVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.GoodsResVO;
import com.hbhb.cw.publicity.web.vo.GoodsVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoods;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsVO;
import com.hbhb.cw.publicity.web.vo.VerifyGoodsVO;
import com.hbhb.cw.publicity.web.vo.VerifyHallGoodsReqVO;
import com.hbhb.cw.publicity.web.vo.VerifyHallGoodsVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;

import org.springframework.beans.BeanUtils;
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
 * @since 2020-11-24
 */
@Service
@Slf4j
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private ApplicationMapper applicationMapper;
    @Resource
    private ApplicationDetailMapper applicationDetailMapper;
    @Resource
    private GoodsSettingService goodsSettingService;
    @Resource
    private SysUserApiExp sysUserApiExp;


    @Override
    public GoodsResVO getList( GoodsCondVO goodsCondVO) {
        if (goodsCondVO.getHallId() == null) {
            // 报异常
            throw new GoodsException(GoodsErrorCode.NOT_SERVICE_HALL);
        }
        GoodsReqVO goodsReqVO = new GoodsReqVO();
        BeanUtils.copyProperties(goodsCondVO,goodsReqVO);
        // 通过营业厅得到该营业厅下该时间的申请详情
        List<GoodsVO> goods = goodsMapper.selectByCond(goodsReqVO);
        // 得到所有产品
        List<GoodsVO> list = goodsMapper.selectByCond(new GoodsReqVO());
        // 赋值于Goods， 若无，则为申请数量为0
        Map<Long, GoodsVO> map = new HashMap<>();
        for (GoodsVO good : goods) {
            map.put(good.getGoodsId(), good);
        }
        for (int i = 0; i < list.size(); i++) {
            if (map.get(list.get(i).getGoodsId()) != null) {
                list.set(i, map.get(list.get(i).getGoodsId()));
            }
        }
        // 申请数量所需条件（置灰或者能使用）
        // 0.通过时间对比截止时间得到为几月的第几次
        GoodsSetting goodsSetting = goodsSettingService.getSetByDate(goodsCondVO.getTime());
        // 1.得到此刻时间，通过截止时间，判断为几月的第几次。如何0的结果与1的结果不符则直接置灰。
        GoodsSetting setting = goodsSettingService.getSetByDate(DateUtil.dateToString(new Date()));
        if (!goodsSetting.getId().equals(setting.getId())) {
            return new GoodsResVO(list, false);
        }
        // 2.得到第几次，判断这次是否提前结束。
        else if (setting.getIsEnd() != null) {
            return new GoodsResVO(list, false);
        }
        // 3.判断本月此次下该分公司是否已保存
        List<Application> applications = applicationMapper.selectApplicationByUnitId(goodsCondVO.getUnitId(),
                DateUtil.formatDate(setting.getDeadline(), "yyyy-MM"), setting.getGoodsIndex());
        if (applications != null && applications.get(0).getEditable()) {
            return new GoodsResVO(list, false);
        }
        return new GoodsResVO(list, true);
    }

    @Override
    public Integer getGoodsSetting(String time) {
        // 通过时间与截止时间，判断为几月第几次。
        GoodsSetting goodsSetting = goodsSettingService.getSetByDate(time);
        return goodsSetting.getGoodsIndex();
    }

    @Override
    public void applyGoods(List<ApplicationVO> list, GoodsCondVO goodsCondVO) {
        // 新增产品（如果该营业厅该产品已有则覆盖）
        GoodsSetting goodsSetting = goodsSettingService.getSetByDate(goodsCondVO.getTime());
        Date date = new Date();
        Application application = new Application();
        application.setBatchNum(DateUtil.dateToString(date,"yyyyMM") + goodsSetting.getGoodsIndex());
        application.setCreateTime(date);
        application.setHallId(goodsCondVO.getHallId());
        applicationMapper.insert(application);
        List<ApplicationDetail> applicationDetails = new ArrayList<>();
        goodsCondVO.setGoodsIndex(goodsSetting.getGoodsIndex());
        // 得到id
        Application applicationRes = applicationMapper.selectByHall(goodsCondVO);
        for (int i = 0; i < list.size(); i++) {
            ApplicationDetail applicationDetail = new ApplicationDetail();
            applicationDetail.setApplicationId(applicationRes.getId());
            applicationDetail.setApplyAmount(list.get(i).getApplyAmount());
            applicationDetail.setGoodsId(list.get(i).getGoodsId());
            applicationDetails.add(applicationDetail);
        }
        applicationDetailMapper.insertBatch(applicationDetails);

    }

    @Override
    public SummaryGoodsResVO getSimplexList(GoodsReqVO goodsReqVO) {
        return getSummaryList(goodsReqVO, GoodsType.BUSINESS_SIMPLEX.getValue());
    }

    @Override
    public SummaryGoodsResVO getSingleList(GoodsReqVO goodsReqVO) {
        return getSummaryList(goodsReqVO, GoodsType.FLYER_PAGE.getValue());
    }

    @Override
    public SummaryGoodsResVO getAuditSimplexList(GoodsReqVO goodsReqVO, Integer state) {
        state = state == null ? 0 : state;
        return getSummaryList(goodsReqVO, GoodsType.BUSINESS_SIMPLEX.getValue(), state);
    }

    @Override
    public SummaryGoodsResVO getAuditSingleList(GoodsReqVO goodsReqVO, Integer state) {
        state = state == null ? 0 : state;
        return getSummaryList(goodsReqVO, GoodsType.FLYER_PAGE.getValue(), state);
    }

    @Override
    public void saveGoods(List<Long> list, Integer userId) {
        Date date = new Date();
        // 通过此刻时间与截止时间对比，判断为第几月第几次
        GoodsSetting setting = goodsSettingService.getSetByDate(DateUtil.dateToString(date));
        // 得到第几次，判断此次是否结束。
        if (setting.getIsEnd() != null || setting.getDeadline().getTime() < date.getTime()) {
            // 报异常
            throw new GoodsException(GoodsErrorCode.ALREADY_CLOSE);
        }
        // 保存
        applicationMapper.updateEditable(list);
    }

    @Override
    public void submitGoods(List<Long> list) {
        Date date = new Date();
        // 通过此刻时间与截止时间对比，判断为第几月第几次
        GoodsSetting setting = goodsSettingService.getSetByDate(DateUtil.dateToString(date));
        // 得到第几次，判断此次是否结束。
        if (setting.getIsEnd() != null || setting.getDeadline().getTime() < date.getTime()) {
            // 报异常
            throw new GoodsException(GoodsErrorCode.ALREADY_CLOSE);
        }
        // 提交
        applicationMapper.updateSubmit(list);
        // 判断工作台有没有 ，没有则发送工作台

    }

    @Override
    public void changerModifyAmount(List<GoodsChangerVO> list) {
        // 批量调试修改后申请数量
        applicationMapper.updateBatch(list);
    }


    @Override
    public List<PurchaseGoods> getPurchaseGoodsList(GoodsReqVO goodsReqVO) {
        return goodsMapper.selectPurchaseGoods(goodsReqVO);
    }

    @Override
    public List<VerifyGoodsVO> getVerifyList(Integer userId) {
        UserInfo user = sysUserApiExp.getUserInfoById(userId);
        Date date = new Date();
        // 通过此刻时间与截止时间对比，判断为第几月第几次
        GoodsSetting setting = goodsSettingService.getSetByDate(DateUtil.dateToString(date));
        // 通过时间 ，每次获取其列表
        return goodsMapper.selectVerifyList(user.getNickName(), setting.getDeadline());
    }

    @Override
    public List<VerifyHallGoodsVO> getInfoList(Integer unitId, Long goodsId) {
        Date date = new Date();
        // 通过此刻时间与截止时间对比，判断为第几月第几次
        GoodsSetting setting = goodsSettingService.getSetByDate(DateUtil.dateToString(date));
        return goodsMapper.selectVerifyHallList(VerifyHallGoodsReqVO.builder()
                .goodsId(goodsId)
                .unitId(unitId)
                .goodsIndex(setting.getGoodsIndex())
                .time(DateUtil.dateToString(date, "yyyy-MM"))
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
        goodsMapper.updateVerifyHallBatch(ids);
    }

    @Override
    public List<SummaryUnitGoodsVO> selectUnitSummaryList(GoodsReqVO goodsReqVO, Integer type) {
        // 展示该次该单位下的申请汇总。
        return goodsMapper.selectSummaryUnitByType(goodsReqVO, type, 2);
    }

    /**
     * 获取分公司汇总
     */
    private SummaryGoodsResVO getSummaryList(GoodsReqVO goodsReqVO, Integer type) {
        String date = goodsReqVO.getTime();
        // 通过此刻时间与截止时间对比，判断为第几月第几次
        GoodsSetting goodsSetting = goodsSettingService.getSetByDate(goodsReqVO.getTime());
        // 展示该次该单位下的申请汇总。
        List<SummaryGoodsVO> summaries = goodsMapper.selectSummaryByType(goodsReqVO, type);
        // 得到第几次，判断此次是否结束。如果结束提交置灰
        if (goodsSetting.getIsEnd() != null || goodsSetting.getDeadline().getTime() < DateUtil.stringToDate(date).getTime()) {
            // 如果结束提交置灰
            return new SummaryGoodsResVO(summaries, false);
        }
        // 判断此次是否有过提交,如果已提交，提交置灰
        // 通过单位和时间次序得到所有该次该单位下所有申请表
        List<Application> applicationList = applicationMapper.selectByCond(goodsReqVO);
        for (Application application : applicationList) {
            if (application.getSubmit()) {
                return new SummaryGoodsResVO(summaries, false);
            }
        }
        return new SummaryGoodsResVO(summaries, true);
    }

    /**
     * 获取分公司汇总（审核）
     */
    private SummaryGoodsResVO getSummaryList(GoodsReqVO goodsReqVO, Integer type, Integer state) {
        String date = goodsReqVO.getTime();
        // 通过此刻时间与截止时间对比，判断为第几月第几次
        GoodsSetting goodsSetting = goodsSettingService.getSetByDate(goodsReqVO.getTime());
        // 展示该次该单位下的申请汇总。
        List<SummaryGoodsVO> summaries = goodsMapper.selectSummaryByState(goodsReqVO, type, state);
        // 得到第几次，判断此次是否结束。如果结束提交置灰
        if (goodsSetting.getIsEnd() != null || goodsSetting.getDeadline().getTime() < DateUtil.stringToDate(date).getTime()) {
            // 如果结束提交置灰
            return new SummaryGoodsResVO(summaries, false);
        }
        // 判断此次是否有过提交,如果已提交，提交置灰
        // 通过单位和时间次序得到所有该次该单位下所有申请表
        List<Application> applicationList = applicationMapper.selectByCond(goodsReqVO);
        for (Application application : applicationList) {
            if (application.getSubmit()) {
                return new SummaryGoodsResVO(summaries, false);
            }
        }
        return new SummaryGoodsResVO(summaries, true);
    }
}
