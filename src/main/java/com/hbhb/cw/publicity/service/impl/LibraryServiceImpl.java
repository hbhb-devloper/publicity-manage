package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.model.Activity;
import com.hbhb.cw.publicity.model.Goods;
import com.hbhb.cw.publicity.service.LibraryService;
import com.hbhb.cw.publicity.web.vo.LibraryVO;

import org.springframework.stereotype.Service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-11-23
 */
@Service
@Slf4j
public class LibraryServiceImpl implements LibraryService {
    @Override
    public List<LibraryVO> getTreeList(Integer userId) {
        // 通过id得到起所属单位

        // 得到该单位下的所有活动
        // 转为树形结构

        // 得到该单位下的所有产品

        // 与活动结合转为树形结构
        return null;
    }

    @Override
    public void addLibrary(Integer userId, Boolean flag, Long parentId) {
        // 通过flag判断增加的是产品还是活动

        // 添加
    }

    @Override
    public void addActivity(Integer userId, Activity activity) {
        // 添加活动
    }

    @Override
    public void addGoods(Integer userId, Goods goods) {
        // 添加产品详情
    }

    @Override
    public void updateLibrary(Integer userId, Boolean flag, String name) {
        // 通过flag判断修改的是活动还是产品

        // 修改其名字
    }

    @Override
    public void updateGoodsInfo(Integer userId, Goods goods) {
        // 修改详情仅为产品
    }
}
