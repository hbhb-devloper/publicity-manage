package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoodsResVO;

import org.beetl.sql.core.page.PageResult;

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
    PageResult<PurchaseGoodsResVO> getPurchaseGoodsList(GoodsReqVO goodsReqVO, Integer pageNum, Integer pageSize);

    List<List<String>> getPurchaseGoodsExport(GoodsReqVO cond);

    List<List<String>> getHead(GoodsReqVO cond);
}
