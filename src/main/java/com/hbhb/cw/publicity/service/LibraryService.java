package com.hbhb.cw.publicity.service;

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
    void addLibrary(Integer userId, Boolean flag, Goods goods);

    /**
     * 修改名称
     */
    void updateLibrary(Integer userId, Boolean flag, Goods goods);

    /**
     * 通过id得到物料id
     */
    Goods getInfo(Long id);


}
