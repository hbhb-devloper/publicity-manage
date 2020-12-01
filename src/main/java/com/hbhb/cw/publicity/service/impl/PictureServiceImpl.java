package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.mapper.PictureFileMapper;
import com.hbhb.cw.publicity.mapper.PictureMapper;
import com.hbhb.cw.publicity.model.Picture;
import com.hbhb.cw.publicity.model.PictureFile;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.service.PictureService;
import com.hbhb.cw.publicity.web.vo.PictureFileVO;
import com.hbhb.cw.publicity.web.vo.PictureInfoVO;
import com.hbhb.cw.publicity.web.vo.PictureReqVO;
import com.hbhb.cw.publicity.web.vo.PictureResVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Override
    public PageResult<PictureResVO> getPictureList(PictureReqVO reqVO, Integer pageNum, Integer pageSize) {
        PageRequest<PictureResVO> request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<PictureResVO> list = pictureMapper.selectPictureListByCond(reqVO, request);

        return list;
    }

    @Override
    public PictureInfoVO getPicture(Long id) {
        PictureInfoVO pictureInfoVO = pictureMapper.selectPictureInfoById(id);
        return null;
    }

    @Override
    public void addPicture(PictureInfoVO infoVO, Integer userId) {
        //获取用户姓名
        UserInfo user = userApi.getUserById(userId);
        Picture picture = new Picture();
        BeanUtils.copyProperties(infoVO, picture);
        pictureMapper.insert(picture);
        List<PictureFileVO> fileVOList = infoVO.getFiles();
        List<PictureFile> fileList = new ArrayList<>();
        for (PictureFileVO fileVO : fileVOList) {
            PictureFile pictureFile = new PictureFile();
            pictureFile.setFileId(fileVO.getFileId());
            pictureFile.setPictureId(picture.getId());
            pictureFile.setCreateTime(new Date());
            pictureFile.setCreateBy(user.getNickName());
            fileList.add(pictureFile);
        }
        fileMapper.insertBatch(fileList);

    }
}
