package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.ApplicationNotice;
import com.hbhb.cw.publicity.web.vo.ApplicationNoticeReqVO;
import com.hbhb.cw.publicity.web.vo.ApplicationNoticeResVO;

import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;

/**
 * @author yzc
 * @since 2020-12-04
 */
public interface ApplicationNoticeMapper extends BaseMapper<ApplicationNotice> {
    PageResult<ApplicationNoticeResVO> selectPageByCond(ApplicationNoticeReqVO cond, PageRequest request);
}
