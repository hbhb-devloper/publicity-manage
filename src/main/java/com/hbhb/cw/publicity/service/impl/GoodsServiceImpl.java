package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.mapper.GoodsMapper;
import com.hbhb.cw.publicity.service.GoodsService;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoods;

import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-11-24
 */
@Service
@Slf4j
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public List<PurchaseGoods> getPurchaseGoodsList(GoodsReqVO goodsReqVO) {
        return goodsMapper.selectPurchaseGoods(goodsReqVO);
    }
}
