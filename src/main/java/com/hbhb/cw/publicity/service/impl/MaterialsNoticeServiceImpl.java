package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.mapper.MaterialsNoticeMapper;
import com.hbhb.cw.publicity.model.MaterialsNotice;
import com.hbhb.cw.publicity.service.MaterialsNoticeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * @author wangxiaogang
 */
@Service
public class MaterialsNoticeServiceImpl implements MaterialsNoticeService {
    @Resource
    private MaterialsNoticeMapper noticeMapper;

    @Override
    public void updateNoticeState(Long materialsId) {
        MaterialsNotice notice = new MaterialsNotice();
        notice.setMaterialsId(materialsId);
        notice.setState(1);
        noticeMapper.updateTemplateById(notice);
    }

    @Override
    public void addMaterialsNotice(MaterialsNotice build) {
        noticeMapper.insert(build);
    }
}
