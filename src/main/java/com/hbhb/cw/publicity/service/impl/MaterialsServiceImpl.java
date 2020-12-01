package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.mapper.MaterialsMapper;
import com.hbhb.cw.publicity.service.MaterialsService;
import com.hbhb.cw.publicity.web.vo.MaterialsInfoVO;
import com.hbhb.cw.publicity.web.vo.MaterialsReqVO;
import com.hbhb.cw.publicity.web.vo.MaterialsResVO;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wangxiaogang
 */
@Service
@Slf4j
public class MaterialsServiceImpl implements MaterialsService {
    @Resource
    private MaterialsMapper materialsMapper;

    @Override
    public PageResult<MaterialsResVO> getMaterialsLis(MaterialsReqVO reqVO, Integer pageNum, Integer pageSize) {
        PageRequest<MaterialsResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<MaterialsResVO> Materials = materialsMapper.selectMaterialsListByCond(request, request);
        return null;
    }

    @Override
    public MaterialsInfoVO getMaterials(Long id) {
        MaterialsInfoVO materialsInfoVO = materialsMapper.selectMaterialsById(id);
        return null;
    }

    @Override
    public void addMaterials(MaterialsInfoVO infoVO, Integer userId) {

    }
}
