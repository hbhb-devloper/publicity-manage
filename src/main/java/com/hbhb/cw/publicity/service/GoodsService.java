package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoods;

import java.util.List;

/**
 * @author yzc
 * @since 2020-11-24
 */
public interface GoodsService {

    /**
     * 物料采购汇总
     * @return 物料采购汇总
     */
    List<PurchaseGoods> getPurchaseGoodsList(GoodsReqVO goodsReqVO);
}
