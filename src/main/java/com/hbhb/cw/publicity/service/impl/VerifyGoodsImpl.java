package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.bean.BeanConverter;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.publicity.enums.GoodsErrorCode;
import com.hbhb.cw.publicity.enums.GoodsType;
import com.hbhb.cw.publicity.exception.GoodsException;
import com.hbhb.cw.publicity.mapper.ApplicationMapper;
import com.hbhb.cw.publicity.mapper.GoodsMapper;
import com.hbhb.cw.publicity.model.Application;
import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.cw.publicity.service.GoodsSettingService;
import com.hbhb.cw.publicity.service.VerifyGoodsService;
import com.hbhb.cw.publicity.web.vo.GoodsChangerVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsVO;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
        if (setting.getIsEnd() != null || DateUtil.stringToDate(setting.getDeadline()).getTime() < date.getTime()) {
            // 报异常
            throw new GoodsException(GoodsErrorCode.ALREADY_CLOSE);
        }
        applicationMapper.createLambdaQuery()
                .andIn(Application::getId,list)
                .updateSelective(Application.builder().editable(true).build());
    }

    @Override
    public void submitGoods(List<Long> list) {
        Date date = new Date();
        // 通过此刻时间与截止时间对比，判断为第几月第几次
        GoodsSetting setting = goodsSettingService.getSetByDate(DateUtil.dateToString(date));
        // 得到第几次，判断此次是否结束。
        if (setting.getIsEnd() != null || DateUtil.stringToDate(setting.getDeadline()).getTime() < date.getTime()) {
            // 报异常
            throw new GoodsException(GoodsErrorCode.ALREADY_CLOSE);
        }
        // 提交 applicationMapper.updateSubmit(list);
        applicationMapper.createLambdaQuery()
                .andIn(Application::getId,list)
                .updateSelective(Application.builder().submit(true).build());
        // 判断工作台有没有 ，没有则发送工作台

    }

    @Override
    public void changerModifyAmount(List<GoodsChangerVO> list) {
        List<Application> applicationList = BeanConverter.copyBeanList(list, Application.class);
        // 批量调试修改后申请数量
        applicationMapper.updateBatchTempById(applicationList);
    }

    /**
     * 获取分公司汇总（审核）
     */
    private SummaryGoodsResVO getSummaryList(GoodsReqVO goodsReqVO, Integer type, Integer state) {
        String date = goodsReqVO.getTime();
        // 通过此刻时间与截止时间对比，判断为第几月第几次
        GoodsSetting goodsSetting = goodsSettingService.getSetByDate(goodsReqVO.getTime());
        if (goodsSetting==null){
            return new SummaryGoodsResVO();
        }
        // 展示该次该单位下的申请汇总。
        List<SummaryGoodsVO> summaries = goodsMapper.selectSummaryByState(goodsReqVO, type, state);
        for (int i = summaries.size()-1; i >= 0; i--) {
            if (summaries.get(i).getApplyAmount()==null){
                summaries.remove(i);
            }
        }
        // 得到第几次，判断此次是否结束。如果结束提交置灰
        if (goodsSetting.getIsEnd() != null || DateUtil.stringToDate(goodsSetting.getDeadline()).getTime() < DateUtil.stringToDate(date).getTime()) {
            // 如果结束提交置灰
            return new SummaryGoodsResVO(summaries, false);
        }
        // 判断此次是否有过提交,如果已提交，提交置灰
        // 通过单位和时间次序得到所有该次该单位下所有申请表
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(goodsReqVO.getTime()),"yyyyMM")+goodsReqVO.getGoodsIndex();
        List<Application> applicationList = applicationMapper.selectByCond(goodsReqVO.getUnitId(),batchNum);
        for (Application application : applicationList) {
            if (application.getSubmit()) {
                return new SummaryGoodsResVO(summaries, false);
            }
        }
        return new SummaryGoodsResVO(summaries, true);
    }

}
