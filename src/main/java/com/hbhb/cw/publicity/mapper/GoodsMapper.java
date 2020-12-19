package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.Goods;
import com.hbhb.cw.publicity.web.vo.GoodsCondVO;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.GoodsVO;
import com.hbhb.cw.publicity.web.vo.LibraryVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoodsResVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoodsVO;
import com.hbhb.cw.publicity.web.vo.SummaryCondVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsVO;
import com.hbhb.cw.publicity.web.vo.VerifyGoodsVO;
import com.hbhb.cw.publicity.web.vo.VerifyHallGoodsReqVO;
import com.hbhb.cw.publicity.web.vo.VerifyHallGoodsVO;

import java.util.List;

/**
 * @author wangxiaogang
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVO> selectByCond(GoodsCondVO goodsCondVO);

    List<GoodsVO> selectGoodsList();

    List<LibraryVO> selectByUnitId(Integer unitId);

    List<LibraryVO> selectGoodsByActIds(List<Long> list);

    List<SummaryUnitGoodsVO> selectSummaryUnitByType(SummaryCondVO cond);

    List<SummaryGoodsVO> selectSummaryByState(SummaryCondVO cond);

    List<Long> selectIdsByCond(SummaryCondVO cond);

    List<PurchaseGoodsResVO> selectPurchaseGoods(GoodsReqVO goodsReqVO);

    List<PurchaseGoodsVO> selectGoodsByHallId(GoodsReqVO goodsReqVO);

    List<VerifyGoodsVO> selectVerifyList(Integer userId, String batchNum);

    List<VerifyHallGoodsVO> selectVerifyHallList(VerifyHallGoodsReqVO verifyHallGoodsReqVO);
}
