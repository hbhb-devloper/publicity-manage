package com.hbhb.cw.publicity.service;

import com.hbhb.cw.publicity.web.vo.ApplicationNoticeVO;

/**
 * @author yzc
 * @since 2020-12-04
 */
public interface ApplicationNoticeService {

   void saveApplicationNotice(ApplicationNoticeVO applicationNoticeVO);

   void updateByBatchNum(String batchNum);
}
