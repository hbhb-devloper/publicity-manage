package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.Goods;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.GoodsVO;
import com.hbhb.cw.publicity.web.vo.LibraryVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoodsResVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoodsVO;
import com.hbhb.cw.publicity.web.vo.SummaryCondVO;
import com.hbhb.cw.publicity.web.vo.SummaryGoodsVO;
import com.hbhb.cw.publicity.web.vo.VerifyGoodsVO;
import com.hbhb.cw.publicity.web.vo.VerifyHallGoodsReqVO;
import com.hbhb.cw.publicity.web.vo.VerifyHallGoodsVO;

import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;

import java.util.List;

/**
 * @author wangxiaogang
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVO> selectGoodsList();

    List<LibraryVO> selectByUnitId(Integer unitId);

    List<LibraryVO> selectGoodsByActIds(List<Long> list,Integer unitId);

    List<SummaryGoodsVO> selectSummaryByState(SummaryCondVO cond);

    List<Long> selectIdsByCond(SummaryCondVO cond);

    PageResult<PurchaseGoodsResVO> selectPurchaseGoods( PageRequest<PurchaseGoodsResVO> request,GoodsReqVO goodsReqVO,String batchNum);

    List<PurchaseGoodsVO> selectGoodsByHallId(GoodsReqVO goodsReqVO);

    List<VerifyGoodsVO> selectVerifyList(Integer userId, String batchNum);

    List<VerifyHallGoodsVO> selectVerifyHallList(VerifyHallGoodsReqVO verifyHallGoodsReqVO);
}
