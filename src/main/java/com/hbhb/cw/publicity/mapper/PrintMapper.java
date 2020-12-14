package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.Print;
import com.hbhb.cw.publicity.web.vo.PrintReqVO;
import com.hbhb.cw.publicity.web.vo.PrintResVO;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;

/**
 * @author wangxiaogang
 */
public interface PrintMapper extends BaseMapper<Print> {
    /**
     * 按照条件查询列表
     *
     * @param cond    条件
     * @param request 分页
     * @return 印刷用品列表
     */
    PageResult<PrintResVO> selectPrintByCond(PrintReqVO cond, PageRequest<PrintResVO> request);

}
