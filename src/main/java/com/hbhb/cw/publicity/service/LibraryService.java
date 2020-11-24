package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.model.Activity;
import com.hbhb.cw.publicity.model.Goods;
import com.hbhb.cw.publicity.web.vo.LibraryVO;

import java.util.List;

/**
 * @author yzc
 * @since 2020-11-23
 */
public interface LibraryService {

    /**
     * 通过登录用户得到物料产品库树形列表
     */
    List<LibraryVO> getTreeList(Integer userId);

    /**
     * 新增物料产品
     */
    void addLibrary(Integer userId, Boolean flag, Long parentId);

    /**
     * 新增活动
     */
    void addActivity(Integer userId, Activity activity);

    /**
     * 新增产品
     */
    void addGoods(Integer userId, Goods goods);

    /**
     * 修改名称
     */
    void updateLibrary(Integer userId, Boolean flag, String name);

    /**
     * 修改产品详情
     */
    void updateGoodsInfo(Integer userId, Goods goods);
}
