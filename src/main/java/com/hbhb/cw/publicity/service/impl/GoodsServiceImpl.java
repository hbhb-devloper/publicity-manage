package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.common.GoodsType;
import com.hbhb.cw.publicity.service.GoodsService;
import com.hbhb.cw.publicity.web.vo.ApplicationVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.GoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsVO;

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
        // 1.得到此刻时间，通过截止时间，判断为本月第几次。
        // 2.得到第几次，判断这次是否提前结束。
        // 3.判断本月此次下该分公司是否已保存
        // 4.判断该产品在本月本次是否被物料审核员审核

        return null;
    }

    @Override
    public void applyGoods(List<ApplicationVO> list, Integer userId) {
        // 通过用户id得到用户详情，营业厅

        // 通过营业厅获得该营业厅所属分公司

        // 新增产品（如果该营业厅该产品已有则覆盖）

    }

    @Override
    public SummaryGoodsVO getSimplexList(Integer userId) {
        return getSummaryList(userId, GoodsType.BUSINESS_SIMPLEX.getValue());
    }

    @Override
    public SummaryGoodsVO getSingleList(Integer userId) {
        return getSummaryList(userId, GoodsType.FLYER_PAGE.getValue());
    }

    // 获取分公司汇总
    private SummaryGoodsVO getSummaryList(Integer userId, Integer type){
        // 得到此刻时间，通过截止时间，判断为本月第几次。

        // 得到第几次，判断这次是否提前结束。

        // 展示盖茨申请汇总。

        return null;
    }
}
