//package com.hbhb.cw.publicity.service.impl;
//
//import com.hbhb.cw.publicity.mapper.PictureNoticeMapper;
//import com.hbhb.cw.publicity.model.PictureNotice;
//import com.hbhb.cw.publicity.service.PictureNoticeService;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//
///**
// * @author wangxiaogang
// */
//@Service
//public class PictureNoticeServiceImpl implements PictureNoticeService {
//
//    @Resource
//    private PictureNoticeMapper noticeMapper;
//
//    @Override
//    public void updateNoticeState(Long pictureId) {
//        PictureNotice notice = new PictureNotice();
//        notice.setPictureId(pictureId);
//        notice.setState(1);
//        noticeMapper.updateTemplateById(notice);
//    }
//
//    @Override
//    public void addPictureNotice(PictureNotice build) {
//        noticeMapper.insert(build);
//    }
//}
