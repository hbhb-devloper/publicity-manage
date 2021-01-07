package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.ApplicationNoticeVO;

import java.util.List;

/**
 * @author yzc
 * @since 2021-01-07
 */
public interface VerifyNoticeService {
    /**
     * 获取提醒数量
     *
     * @param userId 用户id
     * @return 数量
     */
    Long countNotice(Integer userId);

    /**
     * 跟据用户id获取提醒内容
     *
     * @param userId 用户id
     * @return 提醒内容
     */
    List<ApplicationNoticeVO> listInvoiceNotice(Integer userId);

    /**
     * 更改提醒状态
     *
     * @param id 提醒id
     */
    void changeNoticeState(Long id);
}
