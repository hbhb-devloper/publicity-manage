package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.GoodsCondAppVO;
import com.hbhb.cw.publicity.web.vo.GoodsCondVO;
import com.hbhb.cw.publicity.web.vo.GoodsResVO;

/**
 * @author yzc
 * @since 2020-12-02
 */
public interface ApplicationService {

    /**
     *
     * 获取用户产品
     * @return 营业厅产品申请信息
     *
     */
    GoodsResVO getList(GoodsCondVO goodsCondVO);

    /**
     * 申请产品数量
     * @param goodsCondAppVO 申请产品
     */
    void applyGoods(GoodsCondAppVO goodsCondAppVO);
}
