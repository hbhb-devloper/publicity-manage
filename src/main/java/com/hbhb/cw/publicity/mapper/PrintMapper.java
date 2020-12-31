package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.Print;
import com.hbhb.cw.publicity.web.vo.PrintReqVO;
import com.hbhb.cw.publicity.web.vo.PrintResVO;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.annotation.Param;

import java.util.Date;
import java.util.List;

/**
 * @author wangxiaogang
 */
public interface PrintMapper extends BaseMapper<Print> {
    /**
     * 按照条件查询列表
     *
     * @param cond    条件
     * @param list    单位列表
     * @param request 分页
     * @return 印刷用品列表
     */
    PageResult<PrintResVO> selectPrintByCond(PrintReqVO cond, List<Integer> list, PageRequest<PrintResVO> request);

    /**
     * 跟据单位id获取编号最大值
     *
     * @param createTime 时间
     * @param unitId     单位id
     * @return 数量
     */
    Integer selectPrintNumCountByUnitId(@Param("createTime") Date createTime, @Param("unitId") Integer unitId);
}
