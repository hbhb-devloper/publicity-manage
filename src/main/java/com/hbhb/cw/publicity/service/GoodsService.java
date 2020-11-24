package com.hbhb.cw.publicity.service;

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
public interface GoodsService {

    /**
     *
     * 获取用户产品
     * @return 营业厅产品申请信息
     *
     */
    GoodsResVO getList(GoodsReqVO goodsReqVO);

    /**
     * 申请产品数量
     * @param list 申请产品
     */
    void applyGoods(List<ApplicationVO> list, GoodsReqVO goodsReqVO);

    /**
     * 通过id得到该用户信息
     */
    UserGoodsVO getUserGoods(Integer userId);

    /**
     * 通过时间获得次序
     */
    Integer getGoodsSetting(String time);

    /**
     *
     * 获取用户产品
     * @return 营业厅产品申请信息
     *
     */
    SummaryGoodsVO getSimplexList(GoodsReqVO goodsReqVO);

    /**
     *
     * 获取用户产品
     * @return 营业厅产品申请信息
     *
     */
    SummaryGoodsVO getSingleList(GoodsReqVO goodsReqVO);
    /**
     *
     * 获取用户产品（审核）
     * @return 营业厅产品申请信息
     *
     */
    SummaryGoodsVO getAuditSimplexList(GoodsReqVO goodsReqVO, Integer state);

    /**
     *
     * 获取用户产品（审核）
     * @return 营业厅产品申请信息
     *
     */
    SummaryGoodsVO getAuditSingleList(GoodsReqVO goodsReqVO, Integer state);
    /**
     *
     * 政企或市场部汇总
     * @return 营业厅产品申请信息
     *
     */
    SummaryGoodsVO getUnitSimplexList(GoodsReqVO goodsReqVO, Integer state);

    /**
     *
     * 政企或市场部汇总
     * @return 营业厅产品申请信息
     *
     */
    SummaryGoodsVO getUnitSingleList(GoodsReqVO goodsReqVO, Integer state);
}
