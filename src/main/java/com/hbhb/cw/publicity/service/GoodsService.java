package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.ApplicationVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.GoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsVO;

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
    void applyGoods(List<ApplicationVO> list, Integer userId);

    /**
     *
     * 获取用户产品
     * @param userId 用户id
     * @return 营业厅产品申请信息
     *
     */
    SummaryGoodsVO getSimplexList(Integer userId);

    /**
     *
     * 获取用户产品
     * @param userId 用户id
     * @return 营业厅产品申请信息
     *
     */
    SummaryGoodsVO getSingleList(Integer userId);
}
