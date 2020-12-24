package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.mapper.ApplicationNoticeMapper;
import com.hbhb.cw.publicity.model.ApplicationNotice;
import com.hbhb.cw.publicity.service.ApplicationNoticeService;
import com.hbhb.cw.publicity.web.vo.ApplicationNoticeVO;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

import javax.annotation.Resource;

/**
 * @author yzc
 * @since 2020-12-04
 */
@Service
public class ApplicationNoticeServiceImpl implements ApplicationNoticeService {


    @Resource
    private ApplicationNoticeMapper applicationNoticeMapper;

    @Override
    public void saveApplicationNotice(ApplicationNoticeVO applicationNoticeVO) {
        ApplicationNotice notice = new ApplicationNotice();
        BeanUtils.copyProperties(applicationNoticeVO, notice);
        notice.setCreateTime(new Date());
        applicationNoticeMapper.insert(notice);
    }

    @Override
    public void updateByBatchNum(String batchNum) {
        applicationNoticeMapper.createLambdaQuery()
                .andEq(ApplicationNotice::getBatchNum,batchNum)
                .updateSelective(ApplicationNotice.builder().state(1).build());
    }
}
