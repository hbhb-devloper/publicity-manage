package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.mapper.ApplicationNoticeMapper;
import com.hbhb.cw.publicity.model.ApplicationNotice;
import com.hbhb.cw.publicity.service.ApplicationNoticeService;
import com.hbhb.cw.publicity.web.vo.ApplicationNoticeVO;

import org.springframework.beans.BeanUtils;

import java.util.Date;

import javax.annotation.Resource;

/**
 * @author yzc
 * @since 2020-12-04
 */
public class ApplicationNoticeServiceImpl implements ApplicationNoticeService {


    @Resource
    private ApplicationNoticeMapper applicationNoticeMapper;

    @Override
    public void saveBudgetProjectNotice(ApplicationNoticeVO applicationNoticeVO) {
        ApplicationNotice notice = new ApplicationNotice();
        BeanUtils.copyProperties(applicationNoticeVO, notice);
        notice.setCreateTime(new Date());
        applicationNoticeMapper.insert(notice);
    }

    @Override
    public void updateByBatchNum(String batchNum) {
        applicationNoticeMapper.updateByBatchNum(batchNum);
    }
}
