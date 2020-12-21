package com.hbhb.cw.publicity.mapper;

import com.hbhb.beetlsql.BaseMapper;
import com.hbhb.cw.publicity.model.MaterialsBudget;
import com.hbhb.cw.publicity.web.vo.MaterialsBudgetResVO;

import java.util.List;

/**
 * @author wangxiaogang
 */
public interface MaterialsBudgetMapper extends BaseMapper<MaterialsBudget> {
    /**
     * 预算控制列表
     *
     * @return 列表
     */
    List<MaterialsBudgetResVO> selectBudgetList();
}
