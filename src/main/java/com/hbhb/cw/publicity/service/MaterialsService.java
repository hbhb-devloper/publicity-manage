package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.MaterialsInfoVO;
import com.hbhb.cw.publicity.web.vo.MaterialsReqVO;
import com.hbhb.cw.publicity.web.vo.MaterialsResVO;
import org.beetl.sql.core.page.PageResult;

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

}
