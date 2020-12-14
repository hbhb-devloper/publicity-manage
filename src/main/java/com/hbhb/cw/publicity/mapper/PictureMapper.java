package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.Picture;
import com.hbhb.cw.publicity.web.vo.PictureReqVO;
import com.hbhb.cw.publicity.web.vo.PictureResVO;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;

/**
 * @author wangxiaogang
 */
public interface PictureMapper extends BaseMapper<Picture> {
    /**
     * 跟据条件查询宣传画面列表
     *
     * @param reqVO   查询条件
     * @param request 分页
     * @return 宣传画面列表
     */
    PageResult<PictureResVO> selectPictureListByCond(PictureReqVO reqVO, PageRequest<PictureResVO> request);

}
