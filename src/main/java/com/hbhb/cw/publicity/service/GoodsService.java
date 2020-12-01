package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.ApplicationVO;
import com.hbhb.cw.publicity.web.vo.GoodsChangerVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.GoodsResVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoods;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsResVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsVO;
import com.hbhb.cw.publicity.web.vo.UnitGoodsStateVO;
import com.hbhb.cw.publicity.web.vo.VerifyGoodsVO;
import com.hbhb.cw.publicity.web.vo.VerifyHallGoodsVO;

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
     * 通过时间获得次序
     */
    Integer getGoodsSetting(String time);

    /**
     *
     * 获取用户产品
     * @return 营业厅产品申请信息
     *
     */
    SummaryGoodsResVO getSimplexList(GoodsReqVO goodsReqVO);

    /**
     *
     * 获取用户产品
     * @return 营业厅产品申请信息
     *
     */
    SummaryGoodsResVO getSingleList(GoodsReqVO goodsReqVO);
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

    /**
     * 分公司提交汇总
     * @return 分公司提交状态
     */
    SummaryUnitGoodsResVO getUnitGoodsList(GoodsReqVO goodsReqVO);

    /**
     *
     * 政企或市场部汇总（详情）
     * @return 营业厅产品申请信息
     *
     */
    List<SummaryUnitGoodsVO> getUnitSimplexList(GoodsReqVO goodsReqVO);

    /**
     *
     * 政企或市场部汇总（详情）
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
     * 物料采购汇总
     * @return 物料采购汇总
     */
    List<PurchaseGoods> getPurchaseGoodsList(GoodsReqVO goodsReqVO);


    /**
     * 物料员审核列表（单位）
     */
    List<VerifyGoodsVO> getVerifyList(Integer userId);

    /**
     * 物料员审核列表（营业厅）
     */
    List<VerifyHallGoodsVO> getInfoList(Integer unitId, Long goodsId);


    /**
     * 物料审核员审核
     */
    void approveUnitGoods(Integer unitId, Long goodsId);
}
