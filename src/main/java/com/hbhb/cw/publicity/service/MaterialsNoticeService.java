package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.model.MaterialsNotice;
import com.hbhb.cw.publicity.web.vo.NoticeReqVO;
import com.hbhb.cw.publicity.web.vo.NoticeResVO;
import com.hbhb.cw.publicity.web.vo.NoticeVO;
import org.beetl.sql.core.page.PageResult;

import java.util.List;

/**
 * @author wangxiaogang
 */
public interface MaterialsNoticeService {

    /**
     * 添加提醒
     *
     * @param build 提醒
     */
    void addMaterialsNotice(MaterialsNotice build);

    /**
     * 跟据用户id获取提醒内容
     *
     * @param userId 用户id
     * @return 提醒内容
     */
    List<NoticeVO> listInvoiceNotice(Integer userId);

    /**
     * 分页查询提醒列表
     *
     * @param noticeVo 提醒条件
     * @param pageNum  页
     * @param pageSize 数量
     * @return 提醒信息
     */
    PageResult<NoticeResVO> pagePrintNotice(NoticeReqVO noticeVo, Integer pageNum, Integer pageSize);

    /**
     * 更改提醒状态
     *
     * @param id 提醒id
     */
    void changeNoticeState(Long id);

    /**
     * 获取提醒数量
     *
     * @param userId 用户id
     * @return 数量
     */
    Long countNotice(Integer userId);
}
