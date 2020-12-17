package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.model.PictureNotice;

/**
 * @author wangxiaogang
 */
public interface PictureNoticeService {
    /**
     * 修改宣传画面审批状态
     *
     * @param pictureId id
     */
    void updateNoticeState(Long pictureId);

    /**
     * 添加提醒
     *
     * @param build 提醒
     */
    void addPictureNotice(PictureNotice build);

}
