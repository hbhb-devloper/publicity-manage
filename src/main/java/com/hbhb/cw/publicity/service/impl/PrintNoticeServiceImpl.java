package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.mapper.PrintNoticeMapper;
import com.hbhb.cw.publicity.model.PrintNotice;
import com.hbhb.cw.publicity.service.PrintNoticeService;
import com.hbhb.cw.publicity.web.vo.PrintNoticeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wangxiaogang
 */
@Service
public class PrintNoticeServiceImpl implements PrintNoticeService {
    @Resource
    private PrintNoticeMapper noticeMapper;


    @Override
    public void addPrintNotice(PrintNoticeVO build) {
        PrintNotice notice = new PrintNotice();
        BeanUtils.copyProperties(notice, build);
        noticeMapper.insert(notice);
    }
}
