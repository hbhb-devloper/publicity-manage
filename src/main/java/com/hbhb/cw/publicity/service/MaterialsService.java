package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.*;
import org.beetl.sql.core.page.PageResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wangxiaogang
 */
public interface MaterialsService {
    /**
     * 跟据条件获取物料设计分页列表
     *
     * @param reqVO    条件实体
     * @param pageNum  页数
     * @param pageSize 条数
     * @return 物料设计分页列表
     */
    PageResult<MaterialsResVO> getMaterialsList(MaterialsReqVO reqVO, Integer pageNum, Integer pageSize);

    /**
     * 跟据id获取物料设计详情
     *
     * @param id id
     * @return 详情
     */
    MaterialsInfoVO getMaterials(Long id);

    /**
     * 添加物料设计
     *
     * @param infoVO 物料设计实体
     * @param userId 用户id
     */
    void addMaterials(MaterialsInfoVO infoVO, Integer userId);

    /**
     * 跟据id删除物料设计
     *
     * @param id id
     */
    void deleteMaterials(Long id);

    /**
     * 修改物料设计
     *
     * @param infoVO 物料设计实体
     * @param userId 用户id
     */
    void updateMaterials(MaterialsInfoVO infoVO, Integer userId);

    /**
     * 批量导入
     *
     * @param dataList      导入列表
     * @param importHeadMap 表头
     */
    void saveMaterials(List<MaterialsImportVO> dataList, Map<Integer, String> importHeadMap, AtomicLong printId);

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
    void toApprove(MaterialsInitVO initVO);

    /**
     * 修改状态
     *
     * @param materialsId 物料id
     * @param state       流程状态
     */
    void updateState(Long materialsId, Integer state);
}
