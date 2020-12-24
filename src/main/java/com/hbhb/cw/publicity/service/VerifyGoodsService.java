package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.GoodsChangerVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsResVO;
import com.hbhb.cw.publicity.web.vo.VerifyGoodsExportVO;

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
    SummaryGoodsResVO getAuditList(GoodsReqVO goodsReqVO);

    /**
     * 分公司保存物料
     */
    void saveGoods(GoodsReqVO goodsReqVO);

    /**
     * 分公司提交物料
     */
    void submitGoods(GoodsReqVO goodsReqVO);

    /**
     * 调整修改申请数量
     */
    void changerModifyAmount(List<GoodsChangerVO> list);

    /**
     * 导出列表
     */
    List<List<VerifyGoodsExportVO>>  getExportList(GoodsReqVO goodsReqVO);
}
