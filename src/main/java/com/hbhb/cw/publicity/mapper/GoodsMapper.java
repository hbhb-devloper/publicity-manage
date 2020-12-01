package com.hbhb.cw.publicity.mapper;

import com.hbhb.cw.publicity.model.Goods;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.GoodsVO;
import com.hbhb.cw.publicity.web.vo.LibraryVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoods;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsVO;
import com.hbhb.cw.publicity.web.vo.SummaryUnitGoodsVO;
import com.hbhb.cw.publicity.web.vo.VerifyGoodsVO;
import com.hbhb.cw.publicity.web.vo.VerifyHallGoodsReqVO;
import com.hbhb.cw.publicity.web.vo.VerifyHallGoodsVO;
import com.hbhb.web.beetlsql.BaseMapper;

import java.util.Date;
import java.util.List;

/**
 * @author wangxiaogang
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVO> selectByCond(GoodsReqVO cond);

    Goods selectById(Long id);

    List<LibraryVO> selectByUnitId(Integer unitId);

    List<LibraryVO> selectGoodsByActIds(List<Long> list);

    List<SummaryUnitGoodsVO> selectSummaryUnitByType(GoodsReqVO goodsReqVO, Integer type, Integer state);

    List<SummaryGoodsVO> selectSummaryByType(GoodsReqVO goodsReqVO, Integer type);

    List<SummaryGoodsVO> selectSummaryByState(GoodsReqVO goodsReqVO, Integer type, Integer state);

    List<PurchaseGoods> selectPurchaseGoods(GoodsReqVO goodsReqVO);

    List<VerifyGoodsVO> selectVerifyList(String nickName, Date time);

    List<VerifyHallGoodsVO> selectVerifyHallList(VerifyHallGoodsReqVO verifyHallGoodsReqVO);

    void updateVerifyHallBatch(List<Long> list);
}
