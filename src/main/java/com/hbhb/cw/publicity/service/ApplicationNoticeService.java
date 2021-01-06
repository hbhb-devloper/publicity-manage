package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.ApplicationNoticeReqVO;
import com.hbhb.cw.publicity.web.vo.ApplicationNoticeResVO;
import com.hbhb.cw.publicity.web.vo.ApplicationNoticeVO;

import org.beetl.sql.core.page.PageResult;

import java.util.List;

/**
 * @author yzc
 * @since 2020-12-04
 */
public interface ApplicationNoticeService {

   void saveApplicationNotice(ApplicationNoticeResVO applicationNoticeVO);

   void updateByBatchNum(String batchNum);

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
    * 分页查询提醒列表
    *
    * @param noticeVo 提醒条件
    * @param pageNum  页
    * @param pageSize 数量
    * @return 提醒信息
    */
   PageResult<ApplicationNoticeResVO> pagePrintNotice(ApplicationNoticeReqVO noticeVo, Integer pageNum, Integer pageSize);

   /**
    * 更改提醒状态
    *
    * @param id 提醒id
    */
   void changeNoticeState(Long id);
}
