package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.Materials;
import com.hbhb.cw.publicity.web.vo.MaterialsBudgetVO;
import com.hbhb.cw.publicity.web.vo.MaterialsReqVO;
import com.hbhb.cw.publicity.web.vo.MaterialsResVO;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;

import java.util.Date;

/**
 * @author wangxiaogang
 */
public interface MaterialsMapper extends BaseMapper<Materials> {
    /**
     * 跟据条件获取宣传物料制作列表
     *
     * @param cond    条件
     * @param request 页数
     * @return 宣传物料制作列表
     */
    PageResult<MaterialsResVO> selectMaterialsListByCond(MaterialsReqVO cond, PageRequest<MaterialsResVO> request);

    /**
     * 跟据单位id统计单位下制作预算信息
     *
     * @param unitId 单位id
     * @return 统计信息
     */
    MaterialsBudgetVO selectMaterialsBudgetByUnitId(Integer unitId);

    /**
     * 获取单位下今年最大编号
     *
     * @param date   时间
     * @param unitId 单位
     * @return 最大编号
     */
    Integer selectPictureNumCountByUnitId(Date date, Integer unitId);
}
