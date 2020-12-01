package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.enums.GoodsErrorCode;
import com.hbhb.cw.publicity.exception.GoodsException;
import com.hbhb.cw.publicity.mapper.GoodsMapper;
import com.hbhb.cw.publicity.model.Goods;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.service.LibraryService;
import com.hbhb.cw.publicity.web.vo.LibraryAddVO;
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
        UserInfo user = userApi.getUserById(userId);
        Integer unitId = user.getUnitId();
        // 得到该单位下的所有活动
        List<LibraryVO> list = goodsMapper.selectByUnitId(unitId);
        // 得到该单位下的所有产品
        List<Long> activities = new ArrayList<>();
        List<LibraryVO> libraries = new ArrayList<>();
        for (LibraryVO libraryVO : list) {
           libraries.addAll(libraryVO.getChildren());
        }
        for (LibraryVO libraryVO : libraries) {
            activities.add(libraryVO.getId());
        }
        // 与活动结合转为树形结构
        List<LibraryVO> goodsList = goodsMapper.selectGoodsByActIds(activities);
        // activityId => Goods
        Map<Long, List<LibraryVO>> map = new HashMap<>();
        for (LibraryVO cond : goodsList) {
            // 判断该活动下是否有货物
            List<LibraryVO> condList = map.get(cond.getId());
            // 如果没有则新建
            if (condList == null) {
                List<LibraryVO> goods = new ArrayList<>();
                goods.add(cond);
                map.put(cond.getId(), goods);
            }
            // 如果有则添加
            else {
                condList.add(cond);
            }
        }
        for (LibraryVO libraryVO : libraries) {
            libraryVO.setChildren(map.get(libraryVO.getId()));
        }
        return list;
    }

    @Override
    public void addLibrary(Integer userId, LibraryAddVO libraryAddVO) {
        // 通过flag判断增加的是产品还是活动， 添加
        // 如果为活动
        if (libraryAddVO.getFlag()){
            if (libraryAddVO.getUnit()==null||libraryAddVO.getGoodsName()==null){
                throw new GoodsException(GoodsErrorCode.NOT_FILLED_IN);
            }
        }
        // 如果为产品
        else {
            // 判断必填项是否添加
            if (libraryAddVO.getGoodsName()==null||libraryAddVO.getGoodsNum()==null||libraryAddVO.getType()==null
            ||libraryAddVO.getChecker()==null||libraryAddVO.getUnit()==null||libraryAddVO.getSize()==null||libraryAddVO.getPaper()==null
            ||libraryAddVO.getUpdateBy()==null|libraryAddVO.getState()){
                throw new GoodsException(GoodsErrorCode.NOT_FILLED_IN);
            }
            // 添加产品
        }
        Goods goods = new Goods();
        BeanUtils.copyProperties(libraryAddVO,goods);
        goodsMapper.insert(goods);
    }

    @Override
    public void updateLibrary(Integer userId, LibraryAddVO libraryAddVO) {
        // 通过flag判断修改的是活动还是产品，启用
        // 如果为活动
        if (libraryAddVO.getFlag()){
            if (libraryAddVO.getUnit()==null||libraryAddVO.getGoodsName()==null){
                throw new GoodsException(GoodsErrorCode.NOT_FILLED_IN);
            }
        }
        // 如果为产品
        else {
            // 判断必填项是否添加
            if (libraryAddVO.getGoodsName()==null||libraryAddVO.getGoodsNum()==null||libraryAddVO.getType()==null
                    ||libraryAddVO.getChecker()==null||libraryAddVO.getUnit()==null||libraryAddVO.getSize()==null||libraryAddVO.getPaper()==null
                    ||libraryAddVO.getUpdateBy()==null|libraryAddVO.getState()){
                throw new GoodsException(GoodsErrorCode.NOT_FILLED_IN);
            }
            // 添加产品
        }
        Goods goods = new Goods();
        BeanUtils.copyProperties(libraryAddVO,goods);
        goodsMapper.updateById(goods);
    }

    @Override
    public Goods getInfo(Long id) {
       return goodsMapper.selectById(id);
    }
}
