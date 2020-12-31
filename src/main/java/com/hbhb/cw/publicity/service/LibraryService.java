package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.model.Goods;
import com.hbhb.cw.publicity.web.vo.CheckerVO;
import com.hbhb.cw.publicity.web.vo.GoodsInfoVO;
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
    List<LibraryVO> getTreeList(Integer userId,Integer unitId);

    /**
     * 新增物料产品
     */
    void addLibrary(Integer userId, Goods libraryAddVO);

    /**
     * 修改名称
     */
    void updateLibrary(Integer userId, Goods libraryAddVO);

    /**
     * 批量修改物料审核员
     */
    void updateBatchChecker(CheckerVO checkerVO);

    /**
     * 通过id得到物料id
     */
    GoodsInfoVO getInfo(Long id);

    /**
     * 通过id删除物料
     */
    void deleteGoods(Long id);

}
