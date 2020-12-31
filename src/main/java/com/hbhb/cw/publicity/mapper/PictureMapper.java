package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.Picture;
import com.hbhb.cw.publicity.web.vo.PictureReqVO;
import com.hbhb.cw.publicity.web.vo.PictureResVO;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.annotation.Param;

import java.util.Date;
import java.util.List;

/**
 * @author wangxiaogang
 */
public interface PictureMapper extends BaseMapper<Picture> {
    /**
     * 跟据条件查询宣传画面列表
     *
     * @param reqVO   查询条件
     * @param request 分页
     * @param list    单位列表
     * @return 宣传画面列表
     */
    PageResult<PictureResVO> selectPictureListByCond(PictureReqVO reqVO, List<Integer> list, PageRequest<PictureResVO> request);

    /**
     * 获取单位下今年最大编号
     *
     * @param createTime 时间
     * @param unitId     单位
     * @return 最大编号
     */
    Integer selectPictureNumCountByUnitId(@Param("createTime") Date createTime, @Param("unitId") Integer unitId);
}
