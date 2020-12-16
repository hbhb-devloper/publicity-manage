package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.bean.BeanConverter;
import com.hbhb.cw.publicity.mapper.PictureFileMapper;
import com.hbhb.cw.publicity.mapper.PictureMapper;
import com.hbhb.cw.publicity.model.Picture;
import com.hbhb.cw.publicity.model.PictureFile;
import com.hbhb.cw.publicity.rpc.FileApiExp;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.rpc.UnitApiExp;
import com.hbhb.cw.publicity.service.PictureService;
import com.hbhb.cw.publicity.web.vo.PictureFileVO;
import com.hbhb.cw.publicity.web.vo.PictureInfoVO;
import com.hbhb.cw.publicity.web.vo.PictureReqVO;
import com.hbhb.cw.publicity.web.vo.PictureResVO;
import com.hbhb.cw.systemcenter.model.SysFile;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.alibaba.excel.util.StringUtils.isEmpty;

/**
 * @author wangxiaogang
 */
@Slf4j
@Service
public class PictureServiceImpl implements PictureService {
    @Resource
    private PictureMapper pictureMapper;
    @Resource
    private PictureFileMapper fileMapper;
    @Resource
    private SysUserApiExp userApi;
    @Resource
    private FileApiExp fileApi;
    @Resource
    private UnitApiExp unitApi;


    @Override
    public PageResult<PictureResVO> getPictureList(PictureReqVO reqVO, Integer pageNum, Integer pageSize) {
        PageRequest<PictureResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<PictureResVO> list = pictureMapper.selectPictureListByCond(reqVO, request);
        List<Integer> userIds = new ArrayList<>();
        Map<Integer, String> unitMapById = unitApi.getUnitMapById();
        list.getList().forEach(item -> userIds.add(item.getUserId()));
        Map<Integer, String> userMap = userApi.getUserMapById(userIds);
        list.getList().forEach(item -> {
            item.setUserName(userMap.get(item.getUserId()));
            item.setUnitName(unitMapById.get(item.getUnitId()));
        });
        return list;
    }

    @Override
    public PictureInfoVO getPicture(Long id) {
        Picture picture = pictureMapper.single(id);
        List<PictureFile> files = fileMapper.createLambdaQuery().andEq(PictureFile::getPictureId, id).select();
        // 获取文件列表信息
        List<Integer> fileIds = new ArrayList<>();
        files.forEach(item -> fileIds.add(Math.toIntExact(item.getFileId())));
        List<SysFile> fileInfoList = fileApi.getFileInfoBatch(fileIds);
        List<PictureFileVO> fileVo = Optional.of(files)
                .orElse(new ArrayList<>())
                .stream()
                .map(file -> PictureFileVO.builder()
                        .author(file.getCreateBy())
                        .createTime(file.getCreateTime().toString())
                        .id(file.getId())
                        .build())
                .collect(Collectors.toList());
        BeanConverter.copyBeanList(fileVo, fileInfoList.getClass());
        PictureInfoVO info = new PictureInfoVO();
        BeanConverter.convert(info, picture.getClass());
        info.setFiles(fileVo);
        return info;
    }

    @Override
    public void addPicture(PictureInfoVO infoVO, Integer userId) {
        //获取用户姓名
        Picture picture = new Picture();
        BeanUtils.copyProperties(infoVO, picture);
        pictureMapper.insert(picture);
        // 保存附件
        List<PictureFile> fileList = setPictureFile(infoVO.getFiles(), userId);
        fileMapper.insertBatch(fileList);

    }

    @Override
    public void updatePicture(PictureInfoVO infoVO, Integer userId) {
        Picture picture = new Picture();
        BeanUtils.copyProperties(picture, infoVO);
        pictureMapper.updateById(picture);
        // 保存附件
        List<PictureFileVO> fileVOList = infoVO.getFiles();
        List<PictureFileVO> files = new ArrayList<>();
        for (PictureFileVO fileVo : fileVOList) {
            if (isEmpty(fileVo.getId())) {
                files.add(fileVo);
            }
        }
        List<PictureFile> fileList = setPictureFile(files, userId);
        fileMapper.insertBatch(fileList);
    }

    @Override
    public void deletePicture(Long id) {
        pictureMapper.deleteById(id);
    }


    private List<PictureFile> setPictureFile(List<PictureFileVO> fileVOList, Integer userId) {
        //获取用户姓名
        UserInfo user = userApi.getUserInfoById(userId);
        List<PictureFile> fileList = new ArrayList<>();
        if (fileVOList != null && !fileVOList.isEmpty()) {
            fileVOList.forEach(item -> fileList.add(PictureFile.builder()
                    .createBy(user.getNickName())
                    .createTime(new Date())
                    .fileId(item.getFileId())
                    .pictureId(item.getPictureId())
                    .build()));
        }
        return fileList;
    }

    @Override
    public void deleteFile(Long fileId) {
        fileMapper.deleteById(fileId);
    }

}
