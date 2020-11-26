package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.enums.GoodsErrorCode;
import com.hbhb.cw.publicity.exception.GoodsException;
import com.hbhb.cw.publicity.mapper.ActivityMapper;
import com.hbhb.cw.publicity.mapper.GoodsMapper;
import com.hbhb.cw.publicity.model.Goods;
import com.hbhb.cw.publicity.service.LibraryService;
import com.hbhb.cw.publicity.web.vo.LibraryReqVO;
import com.hbhb.cw.publicity.web.vo.LibraryVO;
import com.hbhb.cw.systemcenter.api.UserApi;
import com.hbhb.cw.systemcenter.vo.UserInfo;

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
    private UserApi userApi;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private ActivityMapper activityMapper;

    @Override
    public List<LibraryVO> getTreeList(Integer userId) {
        // 通过id得到起所属单位
        UserInfo user = userApi.getUserById(userId);
        Integer unitId = user.getUnitId();
        // 得到该单位下的所有活动
        List<LibraryVO> list = activityMapper.selectByUnitId(unitId);
        // 得到该单位下的所有产品
        List<Long> activities = new ArrayList<>();
        List<LibraryVO> libraries = new ArrayList<>();
        for (LibraryVO libraryVO : list) {
            libraries.addAll(libraryVO.getActivityVOList());
        }
        for (LibraryVO libraryVO : libraries) {
            activities.add(libraryVO.getId());
        }
        // 与活动结合转为树形结构
        List<Goods> goodsList = goodsMapper.selectGoodsByActIds(activities);
        // activityId => Goods
        Map<Long, List<Goods>> map = new HashMap<>();
        for (Goods cond : goodsList) {
            // 判断该活动下是否有货物
            List<Goods> condList = map.get(cond.getActivityId());
            // 如果没有则新建
            if (condList == null) {
                List<Goods> goods = new ArrayList<>();
                goods.add(cond);
                map.put(cond.getActivityId(), goods);
            }
            // 如果有则添加
            else {
                condList.add(cond);
            }
        }
        for (LibraryVO libraryVO : libraries) {
            libraryVO.setGoodsList(map.get(libraryVO.getId()));
        }
        return list;
    }

    @Override
    public void addLibrary(Integer userId, LibraryReqVO libraryReqVO) {
        libraryReqVO.setState(libraryReqVO.getState() == null ? true : libraryReqVO.getState());
        // 通过flag判断增加的是产品还是活动， 添加
        if (libraryReqVO.getFlag()) {
            activityMapper.insertName(userId, libraryReqVO);
        } else {
            if (libraryReqVO.getFlag()) {
                goodsMapper.insertName(libraryReqVO);
            }
            else {
                // 报异常
                throw new GoodsException(GoodsErrorCode.PARENT_NOT_ACTIVITY);
            }
        }
    }

    @Override
    public void addGoods(Goods goods) {
        goods.setState(goods.getState() == null ? true : goods.getState());
        // 添加产品详情
        goodsMapper.updateById(goods);
    }

    @Override
    public void updateLibrary(Integer userId, LibraryReqVO libraryReqVO) {
        // 通过flag判断修改的是活动还是产品，启用
        if (libraryReqVO.getFlag()) {
            activityMapper.updateByCond(userId, libraryReqVO);
        } else {
            if (libraryReqVO.getFlag()) {
                goodsMapper.updateByCond(libraryReqVO);
            }else{
                // 报异常
                throw new GoodsException(GoodsErrorCode.PARENT_NOT_ACTIVITY);
            }
        }
    }

    @Override
    public void updateGoodsInfo(Integer userId, Goods goods) {
        // 修改详情仅为产品
        goodsMapper.updateById(goods);
    }
}
