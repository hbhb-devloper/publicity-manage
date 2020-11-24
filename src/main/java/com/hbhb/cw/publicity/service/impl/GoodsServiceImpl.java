package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.common.GoodsType;
import com.hbhb.cw.publicity.service.GoodsService;
import com.hbhb.cw.publicity.web.vo.ApplicationVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.GoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsVO;
import com.hbhb.cw.publicity.web.vo.UserGoodsVO;

import java.util.List;

/**
 * @author yzc
 * @since 2020-11-24
 */
public class GoodsServiceImpl implements GoodsService {


    @Override
    public GoodsResVO getList(GoodsReqVO goodsReqVO) {
        if (goodsReqVO.getHallId()==null){
            // 报异常
        }
        // 通过营业厅得到该营业厅下本月，本次的申请详情

        // 得到所有产品

        // 赋值于Goods， 若无，则为申请数量为0

        // 申请数量所需条件（置灰或者能使用）
        // 0.通过时间对比截止时间得到为几月的第几次
        // 1.得到此刻时间，通过截止时间，判断为几月的第几次。如何0的结果与1的结果不符则直接置灰。
        // 2.得到第几次，判断这次是否提前结束。
        // 3.判断本月此次下该分公司是否已保存
        // 4.判断该产品在本月本次是否被物料审核员审核

        return null;
    }

    @Override
    public UserGoodsVO getUserGoods(Integer userId) {
        UserGoodsVO userGoodsVO = new UserGoodsVO();
        // 通过userId得到该用户的单位和营业厅

        // 得到此刻时间，通过截止时间得到该次为第几月的第几次

        return userGoodsVO;
    }

    @Override
    public Integer getGoodsSetting(String time) {
        // 通过时间与截止时间，判断为几月第几次。
        return null;
    }

    @Override
    public void applyGoods(List<ApplicationVO> list, GoodsReqVO goodsReqVO) {
        // 新增产品（如果该营业厅该产品已有则覆盖）
    }

    @Override
    public SummaryGoodsVO getSimplexList(GoodsReqVO goodsReqVO) {
        return getSummaryList(goodsReqVO, GoodsType.BUSINESS_SIMPLEX.getValue());
    }

    @Override
    public SummaryGoodsVO getSingleList(GoodsReqVO goodsReqVO) {
        return getSummaryList(goodsReqVO, GoodsType.FLYER_PAGE.getValue());
    }

    @Override
    public SummaryGoodsVO getAuditSimplexList(GoodsReqVO goodsReqVO, Integer state) {
        // todo state需要字典 枚举
        state = state == null ? 0 : state;
        return getSummaryList(goodsReqVO, GoodsType.BUSINESS_SIMPLEX.getValue(),state);
    }

    @Override
    public SummaryGoodsVO getAuditSingleList(GoodsReqVO goodsReqVO, Integer state) {
        // todo state需要字典 枚举
        state = state == null ? 0 : state;
        return getSummaryList(goodsReqVO, GoodsType.FLYER_PAGE.getValue(), state);
    }

    @Override
    public SummaryGoodsVO getUnitSimplexList(GoodsReqVO goodsReqVO, Integer state) {
        // todo state需要字典 枚举
        state = state == null ? 0 : state;
        return getUnitSummaryList(goodsReqVO, GoodsType.BUSINESS_SIMPLEX.getValue(),state);
    }

    @Override
    public SummaryGoodsVO getUnitSingleList(GoodsReqVO goodsReqVO, Integer state) {
        // todo state需要字典 枚举
        state = state == null ? 0 : state;
        return getUnitSummaryList(goodsReqVO, GoodsType.FLYER_PAGE.getValue(), state);
    }


    /**
     * 获取分公司汇总
     */
    private SummaryGoodsVO getSummaryList(GoodsReqVO goodsReqVO, Integer type){

        // 通过时间与截止时间，判断为几月第几次。

        // 得到第几次，判断此次是否结束。

        // 如果结束提交置灰

        // 展示该次该单位下的申请汇总。

        return null;
    }

    /**
     * 获取分公司汇总（审核）
     */
    private SummaryGoodsVO getSummaryList(GoodsReqVO goodsReqVO, Integer type, Integer state){

        // 通过时间与截止时间，判断为几月第几次。

        // 得到第几次，判断此次是否结束。

        // 如果结束提交置灰

        // 展示该次该单位下该状态下的申请汇总。

        return null;
    }

    /**
     * 获取市场部或政企的汇总
     */
    private SummaryGoodsVO getUnitSummaryList(GoodsReqVO goodsReqVO, Integer type, Integer state){

        // 通过时间与截止时间，判断为几月第几次。

        // 得到第几次，判断此次是否结束。

        // 如果结束提交置灰

        // 展示该次该管理部门下该状态下的申请汇总。

        return null;
    }
}
