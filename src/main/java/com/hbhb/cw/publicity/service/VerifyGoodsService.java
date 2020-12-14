package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.GoodsChangerVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsResVO;

import java.util.List;

/**
 * @author yzc
 * @since 2020-12-14
 */
public interface VerifyGoodsService {
    /**
     *
     * 获取用户产品（审核）
     * @return 营业厅产品申请信息
     *
     */
    SummaryGoodsResVO getAuditSimplexList(GoodsReqVO goodsReqVO, Integer state);

    /**
     *
     * 获取用户产品（审核）
     * @return 营业厅产品申请信息
     *
     */
    SummaryGoodsResVO getAuditSingleList(GoodsReqVO goodsReqVO, Integer state);

    /**
     * 分公司保存物料
     */
    void saveGoods(List<Long> list, Integer userId);

    /**
     * 分公司提交物料
     */
    void submitGoods(List<Long> list);

    /**
     * 调整修改申请数量
     */
    void changerModifyAmount(List<GoodsChangerVO> list);
}
