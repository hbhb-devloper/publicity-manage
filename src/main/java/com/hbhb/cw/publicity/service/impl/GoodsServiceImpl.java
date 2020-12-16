package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.mapper.GoodsMapper;
import com.hbhb.cw.publicity.service.GoodsService;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoodsResVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoodsVO;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public List<PurchaseGoodsResVO> getPurchaseGoodsList(GoodsReqVO goodsReqVO) {
        return goodsMapper.selectPurchaseGoods(goodsReqVO);
    }

    @Override
    public List<List<String>> getPurchaseGoodsExport(GoodsReqVO cond) {
        List<PurchaseGoodsVO> list = goodsMapper.selectGoodsByHallId(cond);
        List<List<String>> exports = new ArrayList<>();
        // goodsId => PurchaseGoodsVO
        Map<Long, List<PurchaseGoodsVO>> goodsIdMap = new HashMap<>();
        // hallId => amount(数量)
        Map<Long, String> headMouldMap = new HashMap<>();
        for (PurchaseGoodsVO purchaseGoodsVO : list) {
            // 得到headMap
            headMouldMap.put(purchaseGoodsVO.getHallId(),"");
            // 判断改物料横坐标下是否有值
            if (goodsIdMap.get(purchaseGoodsVO.getGoodsId())==null){
                ArrayList<PurchaseGoodsVO> conds = new ArrayList<>();
                conds.add(purchaseGoodsVO);
                goodsIdMap.put(purchaseGoodsVO.getGoodsId(),conds);
            }
            else {
                List<PurchaseGoodsVO> conds = goodsIdMap.get(purchaseGoodsVO.getGoodsId());
                conds.add(purchaseGoodsVO);
            }
        }
        Set<Long> hallIdList = headMouldMap.keySet();
        Set<Long> goodsIdList = goodsIdMap.keySet();
        for (Long goodsId : goodsIdList) {
            Map<Long, String> headMap = new HashMap<>();
            headMouldMap.putAll(headMap);
            List<PurchaseGoodsVO> purchaseGoodsVOList = goodsIdMap.get(goodsId);
            for (PurchaseGoodsVO purchaseGoodsVO : purchaseGoodsVOList) {
                headMap.put(purchaseGoodsVO.getHallId(),purchaseGoodsVO.getModifyAmount().toString());
            }
            List<String> row = new ArrayList<>();
            row.add(purchaseGoodsVOList.get(0).getGoodsName());
            row.add(purchaseGoodsVOList.get(0).getGoodsNum());
            row.add(purchaseGoodsVOList.get(0).getSize());
            row.add(purchaseGoodsVOList.get(0).getPaper());
            long sum = 0L;
            for (Long hallId : hallIdList) {
                sum = Long.parseLong(headMap.get(hallId))+sum;
            }
            row.add(Long.toString(sum));
            for (Long hallId : hallIdList) {
                row.add(headMap.get(hallId));
            }
            exports.add(row);
        }
        return exports;
    }

    @Override
    public List<List<String>> getHead(GoodsReqVO cond) {
        List<PurchaseGoodsVO> list = goodsMapper.selectGoodsByHallId(cond);
        List<List<String>> headList = new ArrayList<>();
        for (PurchaseGoodsVO purchaseGoodsVO : list) {
            List<String> head = new ArrayList<>();
            head.add(purchaseGoodsVO.getHallName());
            headList.add(head);
        }
        return headList;
    }
}
