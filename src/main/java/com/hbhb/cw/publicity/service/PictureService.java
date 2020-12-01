package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.PictureInfoVO;
import com.hbhb.cw.publicity.web.vo.PictureReqVO;
import com.hbhb.cw.publicity.web.vo.PictureResVO;
import org.beetl.sql.core.page.PageResult;

/**
 * @author wangxiaogang
 */
public interface PictureService {
    /**
     * 跟据条件查询宣传画面列表
     *
     * @param reqVO    查询条件
     * @param pageNum  页数
     * @param pageSize 条数
     * @return 宣传画面列表
     */
    PageResult<PictureResVO> getPictureList(PictureReqVO reqVO, Integer pageNum, Integer pageSize);

    /**
     * 跟据id查询宣传画面详情
     *
     * @param id id
     * @return 宣传画面详情
     */
    PictureInfoVO getPicture(Long id);

    /**
     * 新增宣传画面设计
     *
     * @param infoVO 新增实体
     * @param userId 用户id
     */
    void addPicture(PictureInfoVO infoVO, Integer userId);
}
