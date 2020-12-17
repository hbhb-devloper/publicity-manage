package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.model.MaterialsNotice;

/**
 * @author wangxiaogang
 */
public interface MaterialsNoticeService {
    /**
     * 修改宣传画面审批状态
     *
     * @param materialsId id
     */
    void updateNoticeState(Long materialsId);

    /**
     * 添加提醒
     *
     * @param build 提醒
     */
    void addMaterialsNotice(MaterialsNotice build);
}
