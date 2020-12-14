package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.publicity.enums.GoodsErrorCode;
import com.hbhb.cw.publicity.exception.GoodsException;
import com.hbhb.cw.publicity.mapper.GoodsMapper;
import com.hbhb.cw.publicity.model.Goods;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.service.LibraryService;
import com.hbhb.cw.publicity.web.vo.GoodsInfoVO;
import com.hbhb.cw.publicity.web.vo.LibraryVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-11-23
 */
@Service
@Slf4j
public class LibraryServiceImpl implements LibraryService {

    @Resource
    private SysUserApiExp userApi;
    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public List<LibraryVO> getTreeList(Integer userId) {
        // 通过id得到起所属单位
        UserInfo user = userApi.getUserInfoById(userId);
        Integer unitId = user.getUnitId();
        // 得到该单位下的所有活动
        // todo 需要优化
        List<LibraryVO> list = goodsMapper.selectByUnitId(unitId);
        List<Long> parents = new ArrayList<>();
        for (LibraryVO libraryVO : list) {
            parents.add(libraryVO.getId());
        }
        if (parents.size()==0){
            return list;
        }
        List<LibraryVO> actList = goodsMapper.selectGoodsByActIds(parents);
        Map<Long, List<LibraryVO>> actMap = new HashMap<>();
        for (LibraryVO cond : actList) {
            // 判断该活动下是否有货物
            List<LibraryVO> condList = actMap.get(cond.getId());
            // 如果没有则新建
            if (condList == null) {
                List<LibraryVO> goods = new ArrayList<>();
                goods.add(cond);
                actMap.put(cond.getParentId(), goods);
            }
            // 如果有则添加
            else {
                condList.add(cond);
            }

        }
        for (LibraryVO libraryVO : list) {
            libraryVO.setChildren(actMap.get(libraryVO.getId()));
        }
        // 得到该单位下的所有产品
        List<Long> activities = new ArrayList<>();
//        List<LibraryVO> libraries = new ArrayList<>();
//        for (LibraryVO libraryVO : list) {
//           libraries.addAll(libraryVO.getChildren());
//        }
        for (LibraryVO libraryVO : actList) {
            activities.add(libraryVO.getId());
        }
        if(activities.size()==0){
            return list;
        }
        // 与活动结合转为树形结构
        List<LibraryVO> goodsList = goodsMapper.selectGoodsByActIds(activities);
        // activityId => Goods
        Map<Long, List<LibraryVO>> goodsMap = new HashMap<>();
        for (LibraryVO cond : goodsList) {
            // 判断该活动下是否有货物
            List<LibraryVO> condList = goodsMap.get(cond.getId());
            // 如果没有则新建
            if (condList == null) {
                List<LibraryVO> goods = new ArrayList<>();
                goods.add(cond);
                goodsMap.put(cond.getParentId(), goods);
            }
            // 如果有则添加
            else {
                condList.add(cond);
            }
        }
        for (LibraryVO libraryVO : actList) {
            libraryVO.setChildren(goodsMap.get(libraryVO.getId()));
        }
        return list;
    }

    @Override
    public void addLibrary(Integer userId, Goods libraryAddVO) {
        // 通过flag判断增加的是产品还是活动， 添加
        // 如果为活动
        if (libraryAddVO.getMold()){
            if (libraryAddVO.getUnitId()==null||libraryAddVO.getGoodsName()==null){
                throw new GoodsException(GoodsErrorCode.NOT_FILLED_IN);
            }
        }
        // 如果为产品
        else {
            // 判断必填项是否添加
            if (libraryAddVO.getGoodsName()==null||libraryAddVO.getGoodsNum()==null||libraryAddVO.getType()==null
            ||libraryAddVO.getChecker()==null||libraryAddVO.getUnit()==null||libraryAddVO.getSize()==null||libraryAddVO.getPaper()==null
            ||!libraryAddVO.getState()){
                throw new GoodsException(GoodsErrorCode.NOT_FILLED_IN);
            }
            // 添加产品
        }
        goodsMapper.insert(libraryAddVO);
    }

    @Override
    public void updateLibrary(Integer userId, Goods libraryAddVO) {
        // 通过flag判断修改的是活动还是产品，启用
        // 如果为活动
        if (libraryAddVO.getMold()){
            if (libraryAddVO.getUnitId()==null||libraryAddVO.getGoodsName()==null){
                throw new GoodsException(GoodsErrorCode.NOT_FILLED_IN);
            }
        }
        // 如果为产品
        else {
            // 判断必填项是否添加
            if (libraryAddVO.getGoodsName()==null||libraryAddVO.getGoodsNum()==null||libraryAddVO.getType()==null
                    ||libraryAddVO.getChecker()==null||libraryAddVO.getUnitId()==null||libraryAddVO.getSize()==null||libraryAddVO.getPaper()==null
                    ||libraryAddVO.getUpdateBy()==null|libraryAddVO.getState()){
                throw new GoodsException(GoodsErrorCode.NOT_FILLED_IN);
            }
            // 修改产品
        }
        goodsMapper.createLambdaQuery().andEq(Goods::getId,libraryAddVO.getId()).updateSelective(libraryAddVO);
    }

    @Override
    public GoodsInfoVO getInfo(Long id) {
        Goods goods = goodsMapper.single(id);
        GoodsInfoVO goodsInfo = new GoodsInfoVO();
        BeanUtils.copyProperties(goods,goodsInfo);
        goodsInfo.setUpdateTime(DateUtil.dateToString(goods.getUpdateTime()));
        String checkerName = userApi.getUserInfoById(goods.getChecker()).getNickName();
        String updateName = userApi.getUserInfoById(goods.getUpdateBy()).getNickName();
        goodsInfo.setCheckerName(checkerName);
        goodsInfo.setUpdateName(updateName);
        if (goods.getState()){
            goodsInfo.setState("是");
        }else {
            goodsInfo.setState("否");
        }
        if (goods.getHasNum()){
            goodsInfo.setHasNum("有");
        }else {
            goodsInfo.setHasNum("无");
        }
        if (goods.getHasSeal()){
            goodsInfo.setHasSeal("有");
        }else {
            goodsInfo.setHasSeal("无");
        }
        return goodsInfo;
    }

    @Override
    public void deleteGoods(Long id) {
        goodsMapper.deleteById(id);
    }
}
