package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.publicity.enums.PublicityErrorCode;
import com.hbhb.cw.publicity.exception.PublicityException;
import com.hbhb.cw.publicity.mapper.GoodsSettingMapper;
import com.hbhb.cw.publicity.model.GoodsSetting;
import com.hbhb.cw.publicity.service.GoodsSettingService;
import com.hbhb.cw.publicity.web.vo.GoodsSettingResVO;
import com.hbhb.cw.publicity.web.vo.GoodsSettingVO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
                    .goodsIndex(i + 1)
                    .deadline(deadlineList.get(i))
                    .contents(goodsSettingVO.getContents()).build());
        }
        goodsSettingMapper.createLambdaQuery()
                .andLike(GoodsSetting::getDeadline, "%" + DateUtil.dateToString(new Date(), "yyyy-MM") + "%")
                .delete();
        // 批量新增物料库相关设定
        goodsSettingMapper.insertBatch(goodsSettings);
        System.out.println("结束");
    }

    @Override
    public GoodsSettingResVO getGoodsSetting(String time) {
        // 当前时间
        Date date = new Date();
        // 次序
        List<Integer> goodsIndexList = new ArrayList<>();
        // 得到该月的所有相关设定
        List<GoodsSetting> goodsSettings = goodsSettingMapper
                .createLambdaQuery().andLike(GoodsSetting::getDeadline, time + "%").select();
        if (goodsSettings.size()==0){
            return new GoodsSettingResVO();
        }
        for (GoodsSetting goodsSetting : goodsSettings) {
            goodsIndexList.add(goodsSetting.getGoodsIndex());
        }
        //
        if (DateUtil.dateToString(date,"yyyy-MM").equals(time)) {
            GoodsSetting goodsSetting = goodsSettingMapper.selectSetByDate(DateUtil.dateToString(new Date()));
            if (goodsSetting==null){
                return GoodsSettingResVO.builder().goodsIndexList(goodsIndexList).build();
            }
            return GoodsSettingResVO.builder().goodsIndexList(goodsIndexList)
                    .goodsIndex(goodsSetting.getGoodsIndex()).build();
        } else {
            if (goodsIndexList.size()==0){
                return new GoodsSettingResVO();
            }
            return GoodsSettingResVO.builder().goodsIndexList(goodsIndexList).goodsIndex(1).build();
        }
    }

    @Override
    public GoodsSetting getSetByDate(String time) {
        // 该次
        return goodsSettingMapper.selectSetByDate(time);
    }


    @Override
    public void updateByBatchNum(String batchNum) {
        String year = batchNum.substring(0, 4);
        String month = batchNum.substring(5, 6);
        String time = year + "-" + month;
        String goodsIndex = batchNum.substring(6);
        goodsSettingMapper.createLambdaQuery()
                .andLike(GoodsSetting::getDeadline, time)
                .andEq(GoodsSetting::getGoodsIndex, goodsIndex)
                .updateSelective(GoodsSetting
                        .builder()
                        .isEnd(DateUtil.dateToString(new Date()))
                        .build());
    }

    @Override
    public GoodsSetting getByCond(String time, Integer goodsIndex) {
        if (goodsIndex==null){
            throw new PublicityException(PublicityErrorCode.NOT_NUMBER_IN_MONTH);
        }
        List<GoodsSetting> goodsSetting = goodsSettingMapper.createLambdaQuery()
                .andLike(GoodsSetting::getDeadline, DateUtil.dateToString(DateUtil.stringToDate(time),"yyyy-MM")+"%")
                .andEq(GoodsSetting::getGoodsIndex, goodsIndex)
                .select();
        if (goodsSetting!=null&&goodsSetting.size()!=0){
            return goodsSetting.get(0);
        }
        else {
            return new GoodsSetting();
        }
    }

    @Override
    public void addNextMonthSetting() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Date date1 = new Date();
        Calendar calendar = Calendar.getInstance();
        // 设置为当前时间
        calendar.setTime(date1);
        // 设置为上一个月
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        date1 = calendar.getTime();
        String lastMonth = format.format(date1);
        List<GoodsSetting> goodsSettings = goodsSettingMapper.selectByDate(lastMonth);
        for (GoodsSetting goodsSetting : goodsSettings) {
            goodsSetting.setId(null);
            String deadline = goodsSetting.getDeadline();
            Date date2 = DateUtil.stringToDate(deadline);
            Calendar calendar2 = Calendar.getInstance();
            // 设置为当前时间
            calendar2.setTime(date2);
            // 设置为下一个月
            calendar2.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
            date2 = calendar2.getTime();
            goodsSetting.setDeadline(DateUtil.dateToString(date2));
        }
        goodsSettingMapper.insertBatch(goodsSettings);
    }
}
