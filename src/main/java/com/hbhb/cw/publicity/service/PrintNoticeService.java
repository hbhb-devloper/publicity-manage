package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.PrintNoticeVO;

/**
 * @author wangxiaogang
 */
public interface PrintNoticeService {
    /**
     * 修改提醒状态
     *
     * @param printId 印刷品id
     */
    void updateNoticeState(Long printId);

    /**
     * 添加提醒
     *
     * @param build 提醒实体
     */
    void addPrintNotice(PrintNoticeVO build);
}
