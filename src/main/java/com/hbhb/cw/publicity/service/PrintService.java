package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.PrintInfoVO;
import com.hbhb.cw.publicity.web.vo.PrintReqVO;
import com.hbhb.cw.publicity.web.vo.PrintResVO;
import org.beetl.sql.core.page.PageResult;

/**
 * @author wangxiaogang
 */
public interface PrintService {
    /**
     * 跟据条件获取宣传印刷用品列表
     *
     * @param reqVO    查询条件实体
     * @param pageNum  页数
     * @param pageSize 条数
     * @return 印刷用品列表
     */
    PageResult<PrintResVO> getPrintList(PrintReqVO reqVO, Integer pageNum, Integer pageSize);

    /**
     * 跟据条件获取印刷用品详情
     *
     * @param id id
     * @return 印刷用品详情
     */
    PrintInfoVO getPrint(Long id);

    /**
     * 新增印刷用品
     *
     * @param infoVO 新增实体
     * @param userId 用户id
     */
    void addPrint(PrintInfoVO infoVO, Integer userId);

    /**
     * 跟据id删除印刷用品
     *
     * @param id id
     */
    void deletePrint(Long id);

    /**
     * 修改印刷用品
     *
     * @param infoVO 修改实体
     * @param userId 用户id
     */
    void updatePrint(PrintInfoVO infoVO, Integer userId);

}
