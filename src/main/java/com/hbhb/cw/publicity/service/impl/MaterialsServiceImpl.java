package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.service.MaterialsService;
import com.hbhb.cw.publicity.web.vo.MaterialsInfoVO;
import com.hbhb.cw.publicity.web.vo.MaterialsReqVO;
import com.hbhb.cw.publicity.web.vo.MaterialsResVO;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;

/**
 * @author wangxiaogang
 */
@Service
@Slf4j
public class MaterialsServiceImpl implements MaterialsService {
    @Override
    public PageResult<MaterialsResVO> getMaterialsLis(MaterialsReqVO reqVO, Integer pageNum, Integer pageSize) {
        return null;
    }

    @Override
    public MaterialsResVO getMaterials(Long id) {
        return null;
    }

    @Override
    public void addMaterials(MaterialsInfoVO infoVO, Integer userId) {

    }
}
