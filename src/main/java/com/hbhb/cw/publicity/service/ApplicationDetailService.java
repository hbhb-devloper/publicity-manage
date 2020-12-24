package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.ApplicationApproveVO;
import com.hbhb.cw.publicity.web.vo.GoodsApproveVO;
import com.hbhb.cw.publicity.web.vo.GoodsCheckerResVO;
import com.hbhb.cw.publicity.web.vo.GoodsCheckerVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.SummaryByUnitVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsVO;
import com.hbhb.cw.publicity.web.vo.UnitGoodsStateVO;
import com.hbhb.cw.publicity.web.vo.VerifyGoodsVO;
import com.hbhb.cw.publicity.web.vo.VerifyHallGoodsVO;

import java.util.List;

/**
 * @author yzc
 * @since 2020-12-14
 */
public interface ApplicationDetailService {

    /**
     * 分公司提交汇总
     * @return 分公司提交状态
     */
    SummaryUnitGoodsResVO getUnitGoodsList(GoodsReqVO goodsReqVO);

    /**
     *
     * 政企或市场部汇总（详情/申请数量）
     * @return 营业厅产品申请信息
     *
     */
    SummaryByUnitVO getInfoList(GoodsReqVO goodsReqVO);


    /**
     * 分公司提交状态
     * @return 分公司提交状态
     */
    List<UnitGoodsStateVO> getUnitGoodsStateList(GoodsReqVO goodsReqVO);

    /**
     * 获取市场部或政企的汇总
     */
    List<SummaryUnitGoodsVO> getUnitSummaryList(GoodsReqVO goodsReqVO, Integer type);

    /**
     * 物料员审核列表（单位）
     */
    List<VerifyGoodsVO> getList(Integer userId);

    /**
     * 物料员审核列表（营业厅）
     */
    List<VerifyHallGoodsVO> getHallList(GoodsCheckerVO goodsCheckerVO);


    /**
     * 物料审核员审核
     */
    void approveUnitGoods(GoodsCheckerResVO goodsCheckerVO);

    /**
     * 发起发票审批
     */
    void toApprover(GoodsApproveVO goodsApproveVO, Integer userId);

    /**
     * 审批节点
     */
    void approve(ApplicationApproveVO approveVO, Integer userId);
}
