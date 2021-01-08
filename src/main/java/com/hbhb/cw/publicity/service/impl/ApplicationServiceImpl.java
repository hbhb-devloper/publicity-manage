package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.publicity.mapper.ApplicationDetailMapper;
import com.hbhb.cw.publicity.mapper.ApplicationMapper;
import com.hbhb.cw.publicity.mapper.GoodsFileMapper;
import com.hbhb.cw.publicity.mapper.GoodsMapper;
import com.hbhb.cw.publicity.model.Application;
import com.hbhb.cw.publicity.model.ApplicationDetail;
import com.hbhb.cw.publicity.model.Goods;
import com.hbhb.cw.publicity.model.GoodsFile;
import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.cw.publicity.rpc.FileApiExp;
import com.hbhb.cw.publicity.service.ApplicationService;
import com.hbhb.cw.publicity.service.GoodsSettingService;
import com.hbhb.cw.publicity.web.vo.ApplicationVO;
import com.hbhb.cw.publicity.web.vo.GoodsCondAppVO;
import com.hbhb.cw.publicity.web.vo.GoodsCondVO;
import com.hbhb.cw.publicity.web.vo.GoodsResVO;
import com.hbhb.cw.publicity.web.vo.GoodsVO;
import com.hbhb.cw.publicity.web.vo.PublicityPictureVO;
import com.hbhb.cw.systemcenter.model.SysFile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

/**
 * @author yzc
 * @since 2020-12-02
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Resource
    private GoodsSettingService goodsSettingService;
    @Resource
    private ApplicationMapper applicationMapper;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private GoodsFileMapper goodsFileMapper;
    @Resource
    private ApplicationDetailMapper applicationDetailMapper;
    @Resource
    private FileApiExp fileApi;
    @Value("${mail.enable}")
    private Boolean mailEnable;

    @Override
    public GoodsResVO getList(GoodsCondVO goodsCondVO) {
        if (goodsCondVO.getHallId() == null) {
            // 报异常
            return new GoodsResVO();
        }
        GoodsSetting goodsSetting = null;
        if (goodsCondVO.getTime() != null && goodsCondVO.getGoodsIndex() == null) {
            return new GoodsResVO();
        }
        if (goodsCondVO.getTime() == null) {
            goodsCondVO.setTime(DateUtil.dateToString(new Date()));
            // 通过时间判断批次
            goodsSetting = goodsSettingService.getSetByDate(DateUtil.dateToString(new Date()));
        } else {
            goodsSetting = goodsSettingService.getByCond(goodsCondVO.getTime(), goodsCondVO.getGoodsIndex());
        }
        if (goodsCondVO.getGoodsIndex() == null) {
            goodsCondVO.setGoodsIndex(goodsSetting.getGoodsIndex());
        }
        if (goodsCondVO.getBatchNum() == null) {
            goodsCondVO.setBatchNum(
                    DateUtil.dateToString(DateUtil.stringToDate(goodsSetting.getDeadline()), "yyyyMM")
                            + goodsCondVO.getGoodsIndex());
        }
        String contents = goodsSetting.getContents();
        // 通过营业厅得到该营业厅下该时间的申请详情
        List<GoodsVO> goods = applicationMapper.selectByCond(goodsCondVO);
        // 得到所有产品
        List<GoodsVO> list = goodsMapper.selectGoodsList();
        for (GoodsVO goodsVO : list) {
            goodsVO.setApplyAmount(0L);
        }
        // 赋值于Goods， 若无，则为申请数量为0
        Map<Long, GoodsVO> map = new HashMap<>();
        for (GoodsVO good : goods) {
            map.put(good.getGoodsId(), good);
        }
        for (int i = 0; i < list.size(); i++) {
            if (map.get(list.get(i).getGoodsId()) != null) {
                list.set(i, map.get(list.get(i).getGoodsId()));
            }
        }
        // 得到所有货物的id，通过id得到所有货物的图片
        List<Long> goodsIds = new ArrayList<>();
        for (GoodsVO good : list) {
            goodsIds.add(good.getGoodsId());
        }
        // 获取所有图片
        List<GoodsFile> picList = goodsFileMapper.createLambdaQuery()
                .andIn(GoodsFile::getGoodsId, goodsIds).select();
        List<PublicityPictureVO> fileList = new ArrayList<>();
        if (picList.size() != 0) {
            List<Integer> fileIds = new ArrayList<>();
            picList.forEach(item -> fileIds.add(Math.toIntExact(item.getFileId())));
            List<SysFile> fileInfoList = fileApi.getFileInfoBatch(fileIds);
            // fileId => sysFile
            Map<Long, SysFile> fileInfoMap = fileInfoList.stream()
                    .collect(Collectors.toMap(SysFile::getId, Function.identity()));
            for (GoodsFile goodsFile : picList) {
                fileList.add(PublicityPictureVO.builder()
                        .goodsId(goodsFile.getGoodsId())
                        .fileName(fileInfoMap.get(goodsFile.getFileId()).getFileName())
                        .filePath(fileInfoMap.get(goodsFile.getFileId()).getFilePath())
                        .fileSize(fileInfoMap.get(goodsFile.getFileId()).getFileSize())
                        .author(goodsFile.getAuthor())
                        .id(goodsFile.getFileId())
                        .build());
            }
        }
        // goodsId => pic
        Map<Long, PublicityPictureVO> picMap = fileList.stream()
                .collect(Collectors.toMap(PublicityPictureVO::getGoodsId, Function.identity()));
        for (GoodsVO goodsVO : list) {
            goodsVO.setPic(picMap.get(goodsVO.getGoodsId()));
        }
        // 申请数量所需条件（置灰或者能使用）
        // 0.通过时间对比截止时间得到为
        // 1.得到此刻时间，通过截止时间，判断为几月的第几次。如何0的结果与1的结果不符则直接置灰。
        GoodsSetting setting = goodsSettingService.getSetByDate(DateUtil.dateToString(new Date()));
        if (setting == null || !goodsSetting.getId().equals(setting.getId())) {
            return new GoodsResVO(list, false, contents);
        }
        // 2.得到第几次，判断这次是否提前结束。
        else if (setting.getIsEnd() != null) {
            return new GoodsResVO(list, false, contents);
        }
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(setting.getDeadline()), "yyyyMM") + goodsSetting.getGoodsIndex();
        // 3.判断本月此次下该分公司是否已保存
        List<Application> applications = applicationMapper.selectApplicationByUnitId(goodsCondVO.getUnitId(),
                goodsCondVO.getHallId(),
                batchNum);
        if (applications != null && applications.size() != 0 && applications.get(0).getEditable()) {
            return new GoodsResVO(list, false, contents);
        }
        return new GoodsResVO(list, true, contents);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyGoods(GoodsCondAppVO goodsCondAppVO) {
        GoodsSetting goodsSetting = null;
        if (goodsCondAppVO.getTime() != null && goodsCondAppVO.getGoodsIndex() == null) {
            return;
        }
        // 判断是否有时间如果没有则默认当前时间
        if (goodsCondAppVO.getTime() == null) {
            // 通过时间判断批次
            goodsSetting = goodsSettingService.getSetByDate(DateUtil.dateToString(new Date()));
            goodsCondAppVO.setTime(goodsSetting.getDeadline());
        } else {
            goodsSetting = goodsSettingService.getByCond(goodsCondAppVO.getTime(), goodsCondAppVO.getGoodsIndex());
        }
        // 如果没有次序且时间与截止时间一样则为该次截止时间
        if (goodsCondAppVO.getGoodsIndex() == null && goodsCondAppVO.getTime().equals(goodsSetting.getDeadline())) {
            goodsCondAppVO.setGoodsIndex(goodsSetting.getGoodsIndex());
        }
        // 得到批次号
        String batchNum = DateUtil.dateToString(DateUtil.stringToDate(goodsSetting.getDeadline()), "yyyyMM")
                + goodsSetting.getGoodsIndex();
        // 得到改营业厅申领单号
        List<Application> applications = applicationMapper.createLambdaQuery()
                .andEq(Application::getBatchNum, batchNum)
                .andEq(Application::getHallId, goodsCondAppVO.getHallId())
                .select();
        List<Long> applicationIds = new ArrayList<>();
        for (Application application : applications) {
            applicationIds.add(application.getId());
        }
        // 删除申领详情
        if (applicationIds.size() != 0) {
            applicationDetailMapper.createLambdaQuery()
                    .andIn(ApplicationDetail::getApplicationId, applicationIds)
                    .delete();
        }
        List<ApplicationVO> list = goodsCondAppVO.getList();
        // 新增产品（如果该营业厅该产品已有则修改）
        Date date = new Date();
        Application application = new Application();
        application.setCreateTime(date);
        application.setUnitId(goodsCondAppVO.getUnitId());
        application.setHallId(goodsCondAppVO.getHallId());
        application.setEditable(false);
        application.setSubmit(false);
        application.setBatchNum(batchNum);
        // 如果存在则修改不存在则删除
        if (applicationIds.size() != 0) {
            application.setId(applicationIds.get(0));
            applicationMapper.updateById(application);
        } else {
            applicationMapper.insert(application);
        }
        List<ApplicationDetail> applicationDetails = new ArrayList<>();
        // 查询修改（新增）后的申领表的id
        Application applicationRes = applicationMapper.selectByHall(goodsCondAppVO.getUnitId(),
                application.getBatchNum(),
                goodsCondAppVO.getHallId());
        List<Goods> goodsList = goodsMapper.createLambdaQuery().select();
        // goodsId => underUnitId
        Map<Long, Integer> map = new HashMap<>();
        for (Goods goods : goodsList) {
            map.put(goods.getId(), goods.getUnitId());
        }
        for (int i = 0; i < list.size(); i++) {
            Long applyAmount = list.get(i).getApplyAmount();
            if (applyAmount == null || applyAmount == 0L) {
                continue;
            }
            ApplicationDetail applicationDetail = new ApplicationDetail();
            applicationDetail.setApplicationId(applicationRes.getId());
            applicationDetail.setApplyAmount(applyAmount);
            applicationDetail.setModifyAmount(applyAmount);
            applicationDetail.setGoodsId(list.get(i).getGoodsId());
            applicationDetail.setState(0);
            applicationDetail.setApprovedState(10);
            applicationDetail.setUnderUnitId(map.get(list.get(i).getGoodsId()));
            applicationDetails.add(applicationDetail);
        }
        applicationDetailMapper.insertBatch(applicationDetails);
    }
}
