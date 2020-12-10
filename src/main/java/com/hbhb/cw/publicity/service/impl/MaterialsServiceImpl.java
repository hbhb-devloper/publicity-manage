package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.mapper.MaterialsFileMapper;
import com.hbhb.cw.publicity.mapper.MaterialsMapper;
import com.hbhb.cw.publicity.model.Materials;
import com.hbhb.cw.publicity.model.MaterialsFile;
import com.hbhb.cw.publicity.rpc.FileApiExp;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.service.MaterialsService;
import com.hbhb.cw.publicity.web.vo.MaterialsFileVO;
import com.hbhb.cw.publicity.web.vo.MaterialsInfoVO;
import com.hbhb.cw.publicity.web.vo.MaterialsReqVO;
import com.hbhb.cw.publicity.web.vo.MaterialsResVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;

import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author wangxiaogang
 */
@Service
@Slf4j
public class MaterialsServiceImpl implements MaterialsService {
    @Resource
    private MaterialsMapper materialsMapper;
    @Resource
    private SysUserApiExp userApi;
    @Resource
    private MaterialsFileMapper fileMapper;
    @Resource
    private FileApiExp fileApiExp;

//    @Override
    public PageResult<MaterialsResVO> getMaterialsLis(MaterialsReqVO reqVO, Integer pageNum, Integer pageSize) {
        PageRequest<MaterialsResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<MaterialsResVO> Materials = materialsMapper.selectMaterialsListByCond(request, request);
        // todo 含字典，单位信息，人员姓名需填充
        return Materials;
    }

    @Override
    public PageResult<MaterialsResVO> getMaterialsList(MaterialsReqVO reqVO, Integer pageNum, Integer pageSize) {
        return null;
    }

    @Override
    public MaterialsInfoVO getMaterials(Long id) {
        MaterialsInfoVO info = materialsMapper.selectMaterialsById(id);
        return info;
    }

    @Override
    public void addMaterials(MaterialsInfoVO infoVO, Integer userId) {
        //获取用户姓名
        UserInfo user = userApi.getUserInfoById(userId);
        Materials materials = new Materials();
        BeanUtils.copyProperties(materials, infoVO);
        // 新增印刷用品
        materialsMapper.insert(materials);
        List<MaterialsFileVO> fileVOList = infoVO.getFiles();
        List<MaterialsFile> fileList = new ArrayList<>();
        for (MaterialsFileVO fileVO : fileVOList) {
            MaterialsFile materialsFile = new MaterialsFile();
            materialsFile.setFileId(fileVO.getFileId());
            materialsFile.setPrintId(materials.getId());
            materialsFile.setCreateTime(new Date());
            materialsFile.setCreateBy(user.getNickName());
            fileList.add(materialsFile);
        }
        fileMapper.insertBatch(fileList);
    }

    @Override
    public void deleteMaterials(Long id) {
        materialsMapper.deleteById(id);
    }

    @Override
    public void updateMaterials(MaterialsInfoVO infoVO, Integer userId) {

    }


}
