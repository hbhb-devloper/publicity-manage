package com.hbhb.cw.publicity.mapper;

import com.hbhb.cw.publicity.model.Print;
import com.hbhb.cw.publicity.web.vo.PrintInfoVO;
import com.hbhb.cw.publicity.web.vo.PrintReqVO;
import com.hbhb.cw.publicity.web.vo.PrintResVO;
import com.hbhb.web.beetlsql.BaseMapper;
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

    /**
     * 跟据id查询宣传印刷品详情
     *
     * @param id id
     * @return 详情
     */
    PrintInfoVO selectPrintInfoById(Long id);
}
