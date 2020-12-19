package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.publicity.mapper.ApplicationDetailMapper;
import com.hbhb.cw.publicity.mapper.ApplicationMapper;
import com.hbhb.cw.publicity.mapper.GoodsMapper;
import com.hbhb.cw.publicity.model.Application;
import com.hbhb.cw.publicity.model.ApplicationDetail;
import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.cw.publicity.service.ApplicationService;
import com.hbhb.cw.publicity.service.GoodsSettingService;
import com.hbhb.cw.publicity.web.vo.ApplicationVO;
import com.hbhb.cw.publicity.web.vo.GoodsCondAppVO;
import com.hbhb.cw.publicity.web.vo.GoodsCondVO;
import com.hbhb.cw.publicity.web.vo.GoodsResVO;
import com.hbhb.cw.publicity.web.vo.GoodsVO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author yzc
 * @since 2020-12-02
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Resource
    private GoodsSettingService goodsSettingService;
    @Resource
    private ApplicationMapper applicationMapper;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private ApplicationDetailMapper applicationDetailMapper;
    @Value("${mail.enable}")
    private Boolean mailEnable;

    @Override
    public GoodsResVO getList(GoodsCondVO goodsCondVO) {
//        if (goodsCondVO.getHallId() == null) {
//            // 报异常
//            throw new GoodsException(GoodsErrorCode.NOT_SERVICE_HALL);
//        }
        if (goodsCondVO.getTime() == null) {
            goodsCondVO.setTime(DateUtil.dateToString(new Date()));
        }
        // 几月的第几次
        GoodsSetting goodsSetting = goodsSettingService.getSetByDate(goodsCondVO.getTime());
        if (goodsCondVO.getGoodsIndex() == null) {
            goodsCondVO.setGoodsIndex(goodsSetting.getGoodsIndex());
        }
        if (goodsCondVO.getBatchNum() == null) {
            goodsCondVO.setBatchNum(
                    DateUtil.dateToString(DateUtil.stringToDate(goodsCondVO.getTime()), "yyyyMM")
                            + goodsCondVO.getGoodsIndex());
        }
        // 通过营业厅得到该营业厅下该时间的申请详情
        List<GoodsVO> goods = goodsMapper.selectByCond(goodsCondVO);
        // 得到所有产品
        List<GoodsVO> list = goodsMapper.selectGoodsList();
        for (GoodsVO goodsVO : list) {
            goodsVO.setApplyAmount(0L);
        }
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
        // 0.通过时间对比截止时间得到为
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
                goodsCondVO.getHallId(),
                DateUtil.dateToString(DateUtil.stringToDate(setting.getDeadline()), "yyyyMM") + setting.getGoodsIndex());
        if (applications != null && applications.size() != 0 && applications.get(0).getEditable()) {
            return new GoodsResVO(list, false);
        }
        return new GoodsResVO(list, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyGoods(GoodsCondAppVO goodsCondAppVO) {
        // 判断是否有时间如果没有则默认当前时间
        if (goodsCondAppVO.getTime() == null) {
            goodsCondAppVO.setTime(DateUtil.dateToString(new Date()));
        }
        // 通过时间判断批次
        GoodsSetting goodsSetting = goodsSettingService.getSetByDate(DateUtil.dateToString(new Date()));
        // 如果没有次序且时间与截止时间一样则为该次截止时间
        if (goodsCondAppVO.getGoodsIndex()==null && goodsCondAppVO.getTime().equals(goodsSetting.getDeadline())) {
            goodsCondAppVO.setGoodsIndex(goodsSetting.getGoodsIndex());
        }
        // 如果没有次序且时间与该次截止时间不一样则为该次截止时间 （本月第一次）
        else if (goodsCondAppVO.getGoodsIndex()==null && !goodsCondAppVO.getTime().equals(goodsSetting.getDeadline())){
            goodsCondAppVO.setGoodsIndex(1);
        }
        // 得到批次号
        String batchNum = DateUtil
                .dateToString(DateUtil.stringToDate(goodsCondAppVO.getTime()), "yyyyMM") + goodsCondAppVO.getGoodsIndex();
        // 得到改营业厅申领单号
        List<Application> applications = applicationMapper.createLambdaQuery()
                .andEq(Application::getBatchNum, batchNum)
                .andEq(Application::getHallId, goodsCondAppVO.getHallId())
                .select();
        List<Long> applicationIds = new ArrayList<>();
        for (Application application : applications) {
            applicationIds.add(application.getId());
        }
        // 删除申领详情
        if (applicationIds.size()!=0) {
            applicationDetailMapper.createLambdaQuery()
                    .andIn(ApplicationDetail::getApplicationId, applicationIds)
                    .delete();
        }
        List<ApplicationVO> list = goodsCondAppVO.getList();
        // 新增产品（如果该营业厅该产品已有则修改）
        Date date = new Date();
        Application application = new Application();
        application.setBatchNum(DateUtil.dateToString(date, "yyyyMM") + goodsSetting.getGoodsIndex());
        application.setCreateTime(date);
        application.setUnitId(goodsCondAppVO.getUnitId());
        application.setHallId(goodsCondAppVO.getHallId());
        application.setEditable(false);
        application.setSubmit(false);
        application.setBatchNum(batchNum);
        // 如果存在则修改不存在则删除
        if (applicationIds.size()!=0) {
            application.setId(applicationIds.get(0));
            applicationMapper.updateById(application);
        }else {
            applicationMapper.insert(application);
        }
        List<ApplicationDetail> applicationDetails = new ArrayList<>();
        // 查询修改（新增）后的申领表的id
        Application applicationRes = applicationMapper.selectByHall(goodsCondAppVO.getUnitId(),
                application.getBatchNum(),
                goodsCondAppVO.getHallId());
        for (int i = 0; i < list.size(); i++) {
            ApplicationDetail applicationDetail = new ApplicationDetail();
            applicationDetail.setApplicationId(applicationRes.getId());
            applicationDetail.setApplyAmount(list.get(i).getApplyAmount());
            applicationDetail.setGoodsId(list.get(i).getGoodsId());
            applicationDetails.add(applicationDetail);
        }
        applicationDetailMapper.insertBatch(applicationDetails);
    }
}
