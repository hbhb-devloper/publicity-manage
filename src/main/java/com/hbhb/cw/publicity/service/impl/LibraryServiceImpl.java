package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.publicity.enums.PublicityErrorCode;
import com.hbhb.cw.publicity.exception.PublicityException;
import com.hbhb.cw.publicity.mapper.GoodsFileMapper;
import com.hbhb.cw.publicity.mapper.GoodsMapper;
import com.hbhb.cw.publicity.model.Goods;
import com.hbhb.cw.publicity.model.GoodsFile;
import com.hbhb.cw.publicity.rpc.FileApiExp;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.service.LibraryService;
import com.hbhb.cw.publicity.web.vo.CheckerVO;
import com.hbhb.cw.publicity.web.vo.GoodsInfoVO;
import com.hbhb.cw.publicity.web.vo.LibraryAddVO;
import com.hbhb.cw.publicity.web.vo.LibraryVO;
import com.hbhb.cw.publicity.web.vo.PublicityPictureVO;
import com.hbhb.cw.systemcenter.model.SysFile;
import com.hbhb.cw.systemcenter.vo.UserInfo;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-11-23
 */
@Service
@Slf4j
public class LibraryServiceImpl implements LibraryService {

    @Resource
    private SysUserApiExp userApi;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private GoodsFileMapper goodsFileMapper;
    @Resource
    private FileApiExp fileApi;

    @Override
    public List<LibraryVO> getTreeList(Integer userId,Integer unitId) {
        // 通过id得到起所属单位
        UserInfo user = userApi.getUserInfoById(userId);
        if (unitId==null) {
             unitId = user.getUnitId();
        }
        // 得到该单位下的所有活动
        List<LibraryVO> list = goodsMapper.selectByUnitId(unitId);
        List<Long> parents = new ArrayList<>();
        for (LibraryVO libraryVO : list) {
            parents.add(libraryVO.getId());
        }
        if (parents.size() == 0) {
            return list;
        }
        List<LibraryVO> actList = goodsMapper.selectGoodsByActIds(parents,unitId);
        Map<Long, List<LibraryVO>> actMap = new HashMap<>();
        for (LibraryVO cond : actList) {
            // 判断该活动下是否有类别
            List<LibraryVO> condList = actMap.get(cond.getParentId());
            // 如果没有则新建
            if (condList == null) {
                List<LibraryVO> goods = new ArrayList<>();
                goods.add(cond);
                actMap.put(cond.getParentId(), goods);
            }
            // 如果有则添加
            else {
                condList.add(cond);
            }

        }
        for (LibraryVO libraryVO : list) {
            libraryVO.setChildren(actMap.get(libraryVO.getId()));
        }
        // 得到该单位下的所有产品
        List<Long> activities = new ArrayList<>();
//        List<LibraryVO> libraries = new ArrayList<>();
//        for (LibraryVO libraryVO : list) {
//           libraries.addAll(libraryVO.getChildren());
//        }
        for (LibraryVO libraryVO : actList) {
            activities.add(libraryVO.getId());
        }
        if (activities.size() == 0) {
            return list;
        }
        // 与活动结合转为树形结构
        List<LibraryVO> goodsList = goodsMapper.selectGoodsByActIds(activities,unitId);
        // activityId => Goods
        Map<Long, List<LibraryVO>> goodsMap = new HashMap<>();
        for (LibraryVO cond : goodsList) {
            // 判断该活动下是否有货物
            List<LibraryVO> condList = goodsMap.get(cond.getParentId());
            // 如果没有则新建
            if (condList == null) {
                List<LibraryVO> goods = new ArrayList<>();
                goods.add(cond);
                goodsMap.put(cond.getParentId(), goods);
            }
            // 如果有则添加
            else {
                condList.add(cond);
            }
        }
        for (LibraryVO libraryVO : actList) {
            libraryVO.setChildren(goodsMap.get(libraryVO.getId()));
        }
        return list;
    }


    @Override
    public void addLibrary(Integer userId, LibraryAddVO cond) {
        Goods libraryAddVO = new Goods();
        BeanUtils.copyProperties(cond,libraryAddVO);
        // 通过id得到起所属单位
        UserInfo user = userApi.getUserInfoById(userId);
        // 通过flag判断增加的是产品还是活动， 添加
        // 如果为活动
        if (libraryAddVO.getMold()) {
            if (libraryAddVO.getUnitId() == null || libraryAddVO.getGoodsName() == null) {
                throw new PublicityException(PublicityErrorCode.NOT_FILLED_IN);
            }
            if (libraryAddVO.getParentId()!=null){
                // 得到其父类
                List<Goods> actGoods = goodsMapper.createLambdaQuery()
                        .andEq(Goods::getId, libraryAddVO.getParentId()).select();
                // 判断父类是否禁用
                if (actGoods != null && actGoods.size() != 0 && !actGoods.get(0).getState()) {
                    throw new PublicityException(PublicityErrorCode.DO_NOT_OPERATE);
                }
                // 判断物料所在位置是否为第三层
                if (actGoods != null && actGoods.size() != 0 && actGoods.get(0).getParentId() != null) {
                    throw new PublicityException(PublicityErrorCode.NOT_ADD_SECONDARY_DIRECTORY);
                }
            }
        }
        // 如果为产品
        else {
            if (libraryAddVO.getParentId() == null) {
                throw new PublicityException(PublicityErrorCode.PLEASE_ADD_SECONDARY_DIRECTORY);
            }
            // 得到其父类，有且只有一个
            List<Goods> actGoods = goodsMapper.createLambdaQuery()
                    .andEq(Goods::getId, libraryAddVO.getParentId()).select();
            if (actGoods != null && actGoods.size() != 0 && !actGoods.get(0).getState()) {
                throw new PublicityException(PublicityErrorCode.DO_NOT_OPERATE);
            }
            // 判断物料所在位置是否为第三层
            if (actGoods == null || actGoods.size() == 0 || actGoods.get(0).getParentId() == null) {
                throw new PublicityException(PublicityErrorCode.PLEASE_ADD_SECONDARY_DIRECTORY);
            }
            // 判断物料所在位置是否为类别
            if (!actGoods.get(0).getMold()) {
                throw new PublicityException(PublicityErrorCode.PLEASE_ADD_SECONDARY_DIRECTORY);
            }
            // 判断必填项是否添加
            if (libraryAddVO.getType() == null
                    || libraryAddVO.getChecker() == null || libraryAddVO.getUnit() == null
                    || libraryAddVO.getSize() == null || libraryAddVO.getPaper() == null) {
                throw new PublicityException(PublicityErrorCode.NOT_FILLED_IN);
            }
            // 添加产品
        }
        libraryAddVO.setUpdateTime(new Date());
        libraryAddVO.setState(true);
        libraryAddVO.setUpdateBy(userId);
        goodsMapper.insert(libraryAddVO);
        PublicityPictureVO file = cond.getFile();
        if (file != null) {
            PublicityPictureVO goodsFileVO = PublicityPictureVO.builder()
                    .author(user.getNickName())
                    .createTime(DateUtil.dateToString(new Date()))
                    .goodsId(libraryAddVO.getId())
                    .fileId(file.getFileId())
                    .build();
            GoodsFile goodsFile = new GoodsFile();
            BeanUtils.copyProperties(goodsFileVO,goodsFile);
            goodsFileMapper.insert(goodsFile);
        }
    }

    @Override
    public void updateLibrary(Integer userId, LibraryAddVO libraryVO) {
        Goods libraryAddVO = new Goods();
        BeanUtils.copyProperties(libraryVO,libraryAddVO);
        // 通过flag判断修改的是活动还是产品，启用
        // 如果为活动
        if (libraryAddVO.getMold()) {
            if (libraryAddVO.getUnitId() == null || libraryAddVO.getGoodsName() == null) {
                throw new PublicityException(PublicityErrorCode.NOT_FILLED_IN);
            }
            if (libraryAddVO.getParentId()!=null) {
                // 得到其父类
                List<Goods> actGoods = goodsMapper.createLambdaQuery()
                        .andEq(Goods::getId, libraryAddVO.getParentId()).select();
                // 判断父类是否禁用
                if (actGoods != null && actGoods.size() != 0 && !actGoods.get(0).getState() && libraryAddVO.getState()) {
                    throw new PublicityException(PublicityErrorCode.DO_NOT_OPERATE);
                }
            }
            // 修改改活动下所有的子类状态
            goodsMapper.createLambdaQuery()
                    .andEq(Goods::getParentId, libraryAddVO.getId())
                    .updateSelective(Goods.builder().state(libraryAddVO.getState()).build());
            // 获取子类信息得到子类id
            List<Goods> goodsList = goodsMapper.createLambdaQuery()
                    .andEq(Goods::getParentId, libraryAddVO.getId())
                    .select();
            List<Long> goodsIds = new ArrayList<>();
            for (Goods goods : goodsList) {
                goodsIds.add(goods.getId());
            }
            // 修改子类下所有状态
            goodsMapper.createLambdaQuery()
                    .andIn(Goods::getParentId, goodsIds)
                    .updateSelective(Goods.builder().state(libraryAddVO.getState()).build());
        }
        // 如果为产品
        else {
            // 得到其父类
            List<Goods> actGoods = goodsMapper.createLambdaQuery()
                    .andEq(Goods::getId, libraryAddVO.getParentId()).select();
            // 判断父类是否禁用
            if (actGoods != null && actGoods.size() != 0 && !actGoods.get(0).getState() && libraryAddVO.getState()) {
                throw new PublicityException(PublicityErrorCode.DO_NOT_OPERATE);
            }
            // 判断必填项是否添加
            if (libraryAddVO.getGoodsName() == null || libraryAddVO.getType() == null
                    || libraryAddVO.getChecker() == null || libraryAddVO.getUnitId() == null || libraryAddVO.getSize() == null || libraryAddVO.getPaper() == null
                    || libraryAddVO.getUpdateBy() == null) {
                throw new PublicityException(PublicityErrorCode.NOT_FILLED_IN);
            }
        }
        // 修改
        libraryAddVO.setUpdateTime(new Date());
        goodsMapper.createLambdaQuery().andEq(Goods::getId, libraryAddVO.getId()).updateSelective(libraryAddVO);
        // 如果图片替换修改file关联id
        if (libraryVO.getFile()!=null){
            List<GoodsFile> goodsFiles = goodsFileMapper.createLambdaQuery()
                    .andEq(GoodsFile::getGoodsId, libraryVO.getId())
                    .select();
            if (goodsFiles.size()!=0){
                GoodsFile goodsFile = goodsFiles.get(0);
                fileApi.deleteFile(goodsFile.getFileId());
            }
            goodsFileMapper.createLambdaQuery().andEq(GoodsFile::getGoodsId,libraryVO.getId())
                    .updateSelective(GoodsFile.builder().fileId(libraryVO.getFile().getFileId())
                    .build());
        }
    }

    @Override
    public void updateBatchChecker(CheckerVO checkerVO) {
        goodsMapper.createLambdaQuery()
                .andEq(Goods::getChecker,checkerVO.getBeforeId())
                .updateSelective(Goods.builder().checker(checkerVO.getAfterId()).build());
    }

    @Override
    public GoodsInfoVO getInfo(Long id) {
        Goods goods = goodsMapper.single(id);
        List<GoodsFile> files = goodsFileMapper.createLambdaQuery().andEq(GoodsFile::getGoodsId, id).select();
        GoodsInfoVO goodsInfo = new GoodsInfoVO();
        if (files.size()!=0){
            GoodsFile goodsFile = files.get(0);
            SysFile file = fileApi.getFileInfo(Math.toIntExact(files.get(0).getFileId()));
            PublicityPictureVO fileVO = PublicityPictureVO.builder()
                    .author(goodsFile.getAuthor())
                    .id(goodsFile.getFileId())
                    .fileName(file.getFileName())
                    .filePath(file.getFilePath())
                    .fileSize(file.getFileSize())
                    .goodsId(id)
                    .build();
            goodsInfo.setFile(fileVO);
        }
        BeanUtils.copyProperties(goods, goodsInfo);
        goodsInfo.setUpdateTime(DateUtil.dateToString(goods.getUpdateTime()));
        String checkerName = userApi.getUserInfoById(goods.getChecker()).getNickName();
        UserInfo user = userApi.getUserInfoById(goods.getUpdateBy());
        goodsInfo.setCheckerName(checkerName);
        goodsInfo.setUpdateName(user.getNickName());
        if (goods.getState()) {
            goodsInfo.setStateLable("是");
        } else {
            goodsInfo.setStateLable("否");
        }
        if (goods.getMold()) {
            return goodsInfo;
        }
        if (goods.getHasNum()) {
            goodsInfo.setHasNumLable("有");
        } else {
            goodsInfo.setHasNumLable("无");
        }
        if (goods.getHasSeal()) {
            goodsInfo.setHasSealLable("有");
        } else {
            goodsInfo.setHasSealLable("无");
        }
        return goodsInfo;
    }

    @Override
    public void deleteGoods(Long id) {
        goodsMapper.deleteById(id);
    }
}
