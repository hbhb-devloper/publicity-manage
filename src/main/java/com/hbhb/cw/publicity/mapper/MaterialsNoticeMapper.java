package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.MaterialsNotice;
import com.hbhb.cw.publicity.web.vo.NoticeReqVO;
import com.hbhb.cw.publicity.web.vo.NoticeResVO;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.annotation.Param;

/**
 * @author wangxiaogang
 */
public interface MaterialsNoticeMapper extends BaseMapper<MaterialsNotice> {
    PageResult<NoticeResVO> selectPageByCond(NoticeReqVO cond, PageRequest request);

    void updateNoticeState(@Param("materialsId") Long materialsId, @Param("state") Integer state);
}
