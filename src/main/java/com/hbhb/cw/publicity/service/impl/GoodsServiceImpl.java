package com.hbhb.cw.publicity.service.impl;

import com.hbhb.api.core.bean.SelectVO;
import com.hbhb.cw.publicity.mapper.GoodsMapper;
import com.hbhb.cw.publicity.rpc.HallApiExp;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.rpc.UnitApiExp;
import com.hbhb.cw.publicity.service.GoodsService;
import com.hbhb.cw.publicity.web.vo.GoodsReqVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoodsResVO;
import com.hbhb.cw.publicity.web.vo.PurchaseGoodsVO;
import com.hbhb.cw.systemcenter.enums.UnitEnum;
import com.hbhb.cw.systemcenter.vo.UserInfo;

import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-11-24
 */
@Service
@Slf4j
@SuppressWarnings(value = {"unchecked"})
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private UnitApiExp unitApiExp;
    @Resource
    private SysUserApiExp sysUserApiExp;
    @Resource
    private HallApiExp hallApiExp;

    @Override
    public PageResult<PurchaseGoodsResVO> getPurchaseGoodsList(GoodsReqVO goodsReqVO, Integer pageNum, Integer pageSize) {
        String batchNum = goodsReqVO.getTime().replace("-","")+goodsReqVO.getGoodsIndex();
        PageRequest<PurchaseGoodsResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<PurchaseGoodsResVO> list = goodsMapper.selectPurchaseGoods(request,goodsReqVO,batchNum);
        Map<Integer, String> unitMap = unitApiExp.getUnitMapById();
        // 得到所有物料的物料审核员id
        List<Integer> userIds = new ArrayList<>();
        for (PurchaseGoodsResVO cond : list.getList()) {
            userIds.add(cond.getCheckerId());
        }
        // 防止userIds等于0
        userIds.add(-1);
        List<UserInfo> userInfoBatch = sysUserApiExp.getUserInfoBatch(userIds);
        Map<Integer, String> userMap = new HashMap<>();
        for (UserInfo infoBatch : userInfoBatch) {
            userMap.put(infoBatch.getId(),infoBatch.getNickName());
        }
        for (PurchaseGoodsResVO cond : list.getList()) {
            cond.setUnitName(unitMap.get(cond.getUnitId()));
            cond.setChecker(userMap.get(cond.getCheckerId()));
        }
        return list;
    }

    @Override
    public List<List<String>> getPurchaseGoodsExport(GoodsReqVO cond) {
        String batchNum = cond.getTime().replace("-","")+cond.getGoodsIndex();
        List<PurchaseGoodsVO> list = goodsMapper.selectGoodsByHallId(cond,batchNum);
        List<PurchaseGoodsVO> hallIds = goodsMapper.selectIdGoodsByHallId(cond,batchNum);
        List<SelectVO> selectVOS = goodsMapper.sumGoodsByHallId(batchNum);
        Map<Long, String> amountMap = selectVOS.stream()
                .collect(Collectors.toMap(SelectVO::getId, SelectVO::getLabel));
        // 得到该单位下所有营业厅map
        Map<Integer, String> map = hallApiExp.selectHallByUnitId(UnitEnum.HANGZHOU.value());
        for (PurchaseGoodsVO purchaseGoodsVO : hallIds) {
            purchaseGoodsVO.setHallName(map.get(Math.toIntExact(purchaseGoodsVO.getHallId())));
        }
        List<List<String>> exports = new ArrayList<>();
        // goodsId => PurchaseGoodsVO
        Map<Long, List<PurchaseGoodsVO>> goodsIdMap = new HashMap<>();
        // hallId => amount(申请数量)
        Map<Long, String> headMouldMap = new HashMap<>();
        for (PurchaseGoodsVO purchaseGoodsVO : list) {
            // 得到headMap
            headMouldMap.put(purchaseGoodsVO.getHallId(),purchaseGoodsVO.getHallName());
            // 判断该物料横坐标下是否有值
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
            row.add(purchaseGoodsVOList.get(0).getSize());
            row.add(purchaseGoodsVOList.get(0).getPaper());
//            row.add(amountMap.get(purchaseGoodsVOList.get(0).getHallId()));
            long sum = 0L;
            // 得到总和
            for (Long hallId : hallIdList) {
                if (headMap.get(hallId)!=null){
                    sum = Long.parseLong(headMap.get(hallId))+sum;
                }
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
        String batchNum = cond.getTime().replace("-","")+cond.getGoodsIndex();
        // 得到该单位下所有营业厅map
        Map<Integer, String> map = hallApiExp.selectHallByUnitId(UnitEnum.HANGZHOU.value());
        List<PurchaseGoodsVO> list = goodsMapper.selectIdGoodsByHallId(cond,batchNum);
        List<List<String>> headList = new ArrayList<>();
        headList.add(new ArrayList<String>(){{this.add("尺寸");}});
        headList.add(new ArrayList<String>(){{this.add("纸张");}});
        headList.add(new ArrayList<String>(){{this.add("数量汇总");}});
        for (PurchaseGoodsVO purchaseGoodsVO : list) {
            List<String> head = new ArrayList<>();
            head.add(map.get(Math.toIntExact(purchaseGoodsVO.getHallId())));
            headList.add(head);
        }
        return headList;
    }
}
