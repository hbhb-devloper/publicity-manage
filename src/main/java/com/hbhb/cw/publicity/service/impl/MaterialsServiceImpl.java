package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.bean.BeanConverter;
import com.hbhb.cw.publicity.mapper.MaterialsFileMapper;
import com.hbhb.cw.publicity.mapper.MaterialsInfoMapper;
import com.hbhb.cw.publicity.mapper.MaterialsMapper;
import com.hbhb.cw.publicity.model.Materials;
import com.hbhb.cw.publicity.model.MaterialsFile;
import com.hbhb.cw.publicity.model.MaterialsInfo;
import com.hbhb.cw.publicity.rpc.FileApiExp;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.rpc.UnitApiExp;
import com.hbhb.cw.publicity.service.MaterialsService;
import com.hbhb.cw.publicity.web.vo.*;
import com.hbhb.cw.systemcenter.model.File;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.alibaba.excel.util.StringUtils.isEmpty;

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
    private FileApiExp fileApi;
    @Resource
    private UnitApiExp unitApi;
    @Resource
    private MaterialsInfoMapper materialsInfoMapper;

    @Override
    public PageResult<MaterialsResVO> getMaterialsList(MaterialsReqVO reqVO, Integer pageNum, Integer pageSize) {
        PageRequest<MaterialsResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        Map<Integer, String> unitMapById = unitApi.getUnitMapById();
        PageResult<MaterialsResVO> materialsList = materialsMapper.selectMaterialsListByCond(request, request);
        List<Integer> userIds = new ArrayList<>();
        materialsList.getList().forEach(item -> userIds.add(item.getUserId()));
        Map<Integer, String> userMapById = userApi.getUserMapById(userIds);
        materialsList.getList().forEach(item -> {
            item.setNickName(userMapById.get(item.getUserId()));
            item.setUnitName(unitMapById.get(item.getUnitId()));

        });
        return materialsList;
    }

    @Override
    public MaterialsInfoVO getMaterials(Long id) {
        Materials materials = materialsMapper.single(id);
        List<MaterialsFile> files = fileMapper.createLambdaQuery().andEq(MaterialsFile::getMaterialsId, id).select();
        // 获取文件列表信息
        List<Integer> fileIds = new ArrayList<>();
        files.forEach(item -> fileIds.add(Math.toIntExact(item.getFileId())));
        List<File> fileInfoList = fileApi.getFileInfoBatch(fileIds);
        List<MaterialsFileVO> fileVo = Optional.of(files)
                .orElse(new ArrayList<>())
                .stream()
                .map(file -> MaterialsFileVO.builder()
                        .author(file.getCreateBy())
                        .createTime(file.getCreateTime().toString())
                        .id(file.getId())
                        .build())
                .collect(Collectors.toList());
        BeanConverter.copyBeanList(fileVo, fileInfoList.getClass());
        MaterialsInfoVO info = new MaterialsInfoVO();
        BeanConverter.convert(info, materials.getClass());
        info.setFiles(fileVo);
        return info;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMaterials(MaterialsInfoVO infoVO, Integer userId) {
        Materials materials = new Materials();
        BeanUtils.copyProperties(materials, infoVO);
        // 新增印刷用品
        materialsMapper.insert(materials);
        List<MaterialsFile> fileList = setMaterialsFile(infoVO.getFiles(), userId);
        fileMapper.insertBatch(fileList);
    }

    @Override
    public void deleteMaterials(Long id) {
        materialsMapper.deleteById(id);
    }

    @Override
    public void updateMaterials(MaterialsInfoVO infoVO, Integer userId) {
        Materials materials = new Materials();
        BeanUtils.copyProperties(materials, infoVO);
        // 新增印刷用品
        materialsMapper.insert(materials);
        // 保存附件
        List<MaterialsFileVO> fileVOList = infoVO.getFiles();
        List<MaterialsFileVO> files = new ArrayList<>();
        for (MaterialsFileVO fileVo : fileVOList) {
            if (isEmpty(fileVo.getId())) {
                files.add(fileVo);
            }
        }
        List<MaterialsFile> fileList = setMaterialsFile(files, userId);
        fileMapper.insertBatch(fileList);
    }

    @Override
    public void saveMaterials(List<MaterialsImportVO> dataList, Map<Integer, String> importHeadMap) {
        //导入
        List<MaterialsInfo> materialsList = new ArrayList<>();
        Map<String, Integer> unitNameMap = unitApi.getUnitMapByUnitName();
        for (MaterialsImportVO importVo : dataList) {
            MaterialsInfo materials = new MaterialsInfo();
            BeanUtils.copyProperties(materials, importVo);
            materials.setUnitId(unitNameMap.get(importVo.getUnitName()));
            materialsList.add(materials);
            // TODO 缺少类型  缺少printId
        }
        materialsInfoMapper.insertBatch(materialsList);
    }

    private List<MaterialsFile> setMaterialsFile(List<MaterialsFileVO> fileVOList, Integer userId) {
        //获取用户姓名
        UserInfo user = userApi.getUserInfoById(userId);
        List<MaterialsFile> fileList = new ArrayList<>();
        if (fileVOList != null && !fileVOList.isEmpty()) {
            fileVOList.forEach(item -> fileList.add(MaterialsFile.builder()
                    .createBy(user.getNickName())
                    .createTime(new Date())
                    .fileId(item.getFileId())
                    .materialsId(item.getMaterialsId())
                    .build()));
        }
        return fileList;
    }

    @Override
    public void deleteFile(Long fileId) {
        fileMapper.deleteById(fileId);
    }


}
