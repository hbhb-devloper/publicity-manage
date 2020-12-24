package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.Picture;
import com.hbhb.cw.publicity.web.vo.PictureReqVO;
import com.hbhb.cw.publicity.web.vo.PictureResVO;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;

import java.util.Date;

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

    /**
     * 获取单位下今年最大编号
     *
     * @param date   时间
     * @param unitId 单位
     * @return 最大编号
     */
    Integer selectPictureNumCountByUnitId(Date date, Integer unitId);
}
