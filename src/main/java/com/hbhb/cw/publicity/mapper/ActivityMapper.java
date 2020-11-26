package com.hbhb.cw.publicity.mapper;

import com.hbhb.cw.publicity.model.Activity;
import com.hbhb.cw.publicity.web.vo.LibraryReqVO;
import com.hbhb.cw.publicity.web.vo.LibraryVO;
import com.hbhb.web.beetlsql.BaseMapper;

import java.util.List;

/**
 * @author wangxiaogang
 */
public interface ActivityMapper extends BaseMapper<Activity> {

    List<LibraryVO> selectByUnitId(Integer unitId);

    void insertName(Integer userId, LibraryReqVO libraryReqVO);

    void updateByCond(Integer userId, LibraryReqVO libraryReqVO);
}
