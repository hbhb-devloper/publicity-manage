package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.publicity.mapper.GoodsSettingMapper;
import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.cw.publicity.service.GoodsSettingService;
import com.hbhb.cw.publicity.web.vo.GoodsSettingResVO;
import com.hbhb.cw.publicity.web.vo.GoodsSettingVO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-11-24
 */
@Service
@Slf4j
public class GoodsSettingServiceImpl implements GoodsSettingService {

    @Resource
    private GoodsSettingMapper goodsSettingMapper;

    @Override
    public List<GoodsSetting> getList() {
        // 得到当前月份
        String time = DateUtil.formatDate(new Date(), "yyyy-MM");
        // 通过月份得到该产品当月的设置
        return goodsSettingMapper.selectByDate(time);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGoodsSetting(GoodsSettingVO goodsSettingVO) {
        List<GoodsSetting> goodsSettings = new ArrayList<>();
        List<String> deadlineList = goodsSettingVO.getDeadlineList();
        for (int i = 0; i < deadlineList.size(); i++) {
            goodsSettings.add(GoodsSetting.builder()
                    .goodsIndex(i+1)
                    .deadline(deadlineList.get(i))
                    .contents(goodsSettingVO.getContents()).build());
        }
        goodsSettingMapper.createLambdaQuery()
                .andLike(GoodsSetting::getDeadline,"%"+DateUtil.dateToString(new Date(),"yyyy-MM")+"%")
                .delete();
        // 批量新增物料库相关设定
        goodsSettingMapper.insertBatch(goodsSettings);
        System.out.println("结束");
    }

    @Override
    public GoodsSettingResVO getGoodsSetting(String time) {
        Date date = new Date();
        List<Integer> goodsIndexList = new ArrayList<>();
        List<GoodsSetting> goodsSettings = goodsSettingMapper
                .createLambdaQuery().andLike(GoodsSetting::getDeadline, time + "%").select();
        for (GoodsSetting goodsSetting : goodsSettings) {
            goodsIndexList.add(goodsSetting.getGoodsIndex());
        }
        if (DateUtil.dateToString(date).equals(time)){
            GoodsSetting goodsSetting = goodsSettingMapper.selectSetByDate(time);
            return GoodsSettingResVO.builder().goodsIndexList(goodsIndexList)
                    .goodsIndex(goodsSetting.getGoodsIndex()).build();
        } else {
            return GoodsSettingResVO.builder().goodsIndexList(goodsIndexList).build();
        }
    }

    @Override
    public GoodsSetting getSetByDate(String time) {
        // 该次
        return goodsSettingMapper.selectSetByDate(time);
    }


    @Override
    public void updateByBatchNum(String batchNum) {
        String time = batchNum.substring(0, 4);
        String goodsIndex = batchNum.substring(4);
        goodsSettingMapper.updateByBatchNum(time, Integer.valueOf(goodsIndex), new Date());
    }
}
