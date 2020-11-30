package com.hbhb.cw.publicity.service.impl;

import com.hbhb.cw.publicity.service.PrintService;
import com.hbhb.cw.publicity.web.vo.PrintInfoVO;
import com.hbhb.cw.publicity.web.vo.PrintReqVO;
import com.hbhb.cw.publicity.web.vo.PrintResVO;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;

/**
 * @author wangxiaogang
 */
@Slf4j
@Service
public class PrintServiceImpl implements PrintService {
    @Override
    public PageResult<PrintResVO> getPrintList(PrintReqVO reqVO, Integer pageNum, Integer pageSize) {
        return null;
    }

    @Override
    public PrintResVO getPrint(Long id) {
        return null;
    }

    @Override
    public void addPrint(PrintInfoVO infoVO, Integer userId) {

    }
}
