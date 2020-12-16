package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.*;
import org.beetl.sql.core.page.PageResult;

import java.util.List;
import java.util.Map;

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

    /**
     * 批量导入
     *
     * @param dataList      导入列表
     * @param importHeadMap 表头
     */
    void savePrint(List<PrintImportVO> dataList, Map<Integer, String> importHeadMap);

    /**
     * 删除附件
     *
     * @param fileId id
     */
    void deleteFile(Long fileId);


    /**
     * 发起审批
     *
     * @param initVO 发起条件
     */
    void toApprove(PrintInitVO initVO);

    /**
     * 修改流程状态
     *
     * @param printId      印刷品id
     * @param projectState 流程状态
     */
    void updateState(Long printId, Integer projectState);
}
