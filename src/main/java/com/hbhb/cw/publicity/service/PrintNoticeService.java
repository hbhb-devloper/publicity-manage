package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.PrintNoticeVO;

/**
 * @author wangxiaogang
 */
public interface PrintNoticeService {
    /**
     * 添加提醒
     *
     * @param build 提醒实体
     */
    void addPrintNotice(PrintNoticeVO build);
}
