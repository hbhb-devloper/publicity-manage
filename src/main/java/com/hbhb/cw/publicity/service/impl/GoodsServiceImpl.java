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
import com.hbhb.cw.publicity.service.GoodsService;
import com.hbhb.cw.publicity.web.vo.ApplicationVO;
import com.hbhb.cw.publicity.web.vo.GoodsChangerVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.GoodsResVO;
import com.hbhb.cw.publicity.web.vo.GoodsVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoods;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsVO;
import com.hbhb.cw.publicity.web.vo.UnitGoodsStateVO;
import com.hbhb.cw.systemcenter.api.DictApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author yzc
 * @since 2020-11-24
 */
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private ApplicationMapper applicationMapper;
    @Resource
    private ApplicationDetailMapper applicationDetailMapper;
    @Resource
    private DictApi dictApi;


    @Override
    public GoodsResVO getList(GoodsReqVO goodsReqVO) {
        if (goodsReqVO.getHallId() == null) {
            // 报异常
            throw new GoodsException(GoodsErrorCode.NOT_SERVICE_HALL);
        }
        // 通过营业厅得到该营业厅下改时间的申请详情
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
        GoodsSetting goodsSetting = goodsMapper.selectSetByDate(goodsReqVO.getTime());
        // 1.得到此刻时间，通过截止时间，判断为几月的第几次。如何0的结果与1的结果不符则直接置灰。
        GoodsSetting setting = goodsMapper.selectSetByDate(DateUtil.dateToString(new Date()));
        if (!goodsSetting.getId().equals(setting.getId())) {
            return new GoodsResVO(list, false);
        }
        // 2.得到第几次，判断这次是否提前结束。
        else if (setting.getIsEnd() != null) {
            return new GoodsResVO(list, false);
        }
        // 3.判断本月此次下该分公司是否已保存
        List<Application> applications = goodsMapper.selectApplicationByUnitId(goodsReqVO.getUnitId(), setting.getDeadline());
        if (applications != null && applications.get(0).getEditable()) {
            return new GoodsResVO(list, false);
        }
        return new GoodsResVO(list, true);
    }

    @Override
    public Integer getGoodsSetting(String time) {
        // 通过时间与截止时间，判断为几月第几次。
        GoodsSetting goodsSetting = goodsMapper.selectSetByDate(time);
        return goodsSetting.getGoodsIndex();
    }

    @Override
    public void applyGoods(List<ApplicationVO> list, GoodsReqVO goodsReqVO) {
        // 新增产品（如果该营业厅该产品已有则覆盖）
        GoodsSetting goodsSetting = goodsMapper.selectSetByDate(goodsReqVO.getTime());
        Date date = new Date();
        List<Application> applications = new ArrayList<>();
        // 生成申请表
        for (ApplicationVO applicationVO : list) {
            Application application = new Application();
            application.setApplyIndex(goodsSetting.getGoodsIndex());
            application.setCreateTime(date);
            application.setHallId(goodsReqVO.getHallId());
            applications.add(application);
        }
        applicationMapper.insertBatch(applications);
        List<ApplicationDetail> applicationDetails = new ArrayList<>();
        // 得到id
        List<Application> applicationList = applicationMapper.selectByCond(goodsReqVO);
        for (int i = 0; i < applicationList.size(); i++) {
            ApplicationDetail applicationDetail = new ApplicationDetail();
            applicationDetail.setApplicationId(applicationList.get(i).getId());
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
        GoodsSetting setting = goodsMapper.selectSetByDate(DateUtil.dateToString(date));
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
        GoodsSetting setting = goodsMapper.selectSetByDate(DateUtil.dateToString(date));
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
    public SummaryUnitGoodsResVO getUnitGoodsList(GoodsReqVO goodsReqVO) {
        String date = goodsReqVO.getTime();
        // 通过此刻时间与截止时间对比，判断为第几月第几次
        GoodsSetting goodsSetting = goodsMapper.selectSetByDate(goodsReqVO.getTime());
        List<SummaryUnitGoodsVO> summaryList = goodsMapper.selectSummaryUnitByCond(goodsReqVO);
        // 得到第几次，判断此次是否结束。
        if (goodsSetting.getIsEnd() != null || goodsSetting.getDeadline().getTime() < DateUtil.stringToDate(date).getTime()) {
            // 如果结束提交置灰
            return new SummaryUnitGoodsResVO(summaryList, false);
        }
        // 展示该次该管理部门下的申请汇总。
        return new SummaryUnitGoodsResVO(summaryList, true);
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
        // 得到所有公司下所有货物（goodsId）
        // 得到该货物的物料审核员（updateBy）和审核状态
        // 通过单位id得到该分公司下的所有单位和状态
        return null;
    }

    @Override
    public List<PurchaseGoods> getPurchaseGoodsList(GoodsReqVO goodsReqVO) {
        return goodsMapper.selectPurchaseGoods(goodsReqVO);
    }

    /**
     * 获取分公司汇总
     */
    private SummaryGoodsResVO getSummaryList(GoodsReqVO goodsReqVO, Integer type) {
        String date = goodsReqVO.getTime();
        // 通过此刻时间与截止时间对比，判断为第几月第几次
        GoodsSetting goodsSetting = goodsMapper.selectSetByDate(goodsReqVO.getTime());
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
        GoodsSetting goodsSetting = goodsMapper.selectSetByDate(goodsReqVO.getTime());
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

    /**
     * 获取市场部或政企的汇总
     */
    private List<SummaryUnitGoodsVO> getUnitSummaryList(GoodsReqVO goodsReqVO, Integer type) {
        // 展示该次该单位下的申请汇总。
        return goodsMapper.selectSummaryUnitByType(goodsReqVO, type, 2);
    }
}
