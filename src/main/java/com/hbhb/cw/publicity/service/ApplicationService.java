package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.ApplicationApproveVO;
import com.hbhb.cw.publicity.web.vo.GoodsApproveVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsVO;
import com.hbhb.cw.publicity.web.vo.UnitGoodsStateVO;

import java.util.List;

/**
 * @author yzc
 * @since 2020-12-02
 */
public interface ApplicationService {

    /**
     * 分公司提交汇总
     * @return 分公司提交状态
     */
    SummaryUnitGoodsResVO getUnitGoodsList(GoodsReqVO goodsReqVO);

    /**
     *
     * 政企或市场部汇总（详情/业务单式申请数量）
     * @return 营业厅产品申请信息
     *
     */
    List<SummaryUnitGoodsVO> getUnitSimplexList(GoodsReqVO goodsReqVO);

    /**
     *
     * 政企或市场部汇总（详情/宣传单页）
     * @return 营业厅产品申请信息
     *
     */
    List<SummaryUnitGoodsVO> getUnitSingleList(GoodsReqVO goodsReqVO);

    /**
     * 分公司提交状态
     * @return 分公司提交状态
     */
    List<UnitGoodsStateVO> getUnitGoodsStateList(GoodsReqVO goodsReqVO);

    /**
     * 发起发票审批
     */
    void toApprover(GoodsApproveVO goodsApproveVO, Integer userId);

    /**
     * 审批节点
     */
    void approve(ApplicationApproveVO approveVO, Integer userId);
}
