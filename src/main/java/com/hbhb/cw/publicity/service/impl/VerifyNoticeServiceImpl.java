package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.publicity.enums.NoticeState;
import com.hbhb.cw.publicity.enums.NoticeType;
import com.hbhb.cw.publicity.mapper.VerifyNoticeMapper;
import com.hbhb.cw.publicity.model.VerifyNotice;
import com.hbhb.cw.publicity.service.VerifyNoticeService;
import com.hbhb.cw.publicity.web.vo.ApplicationNoticeVO;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2021-01-07
 */
@Slf4j
@Service
public class VerifyNoticeServiceImpl implements VerifyNoticeService {

    @Resource
    private VerifyNoticeMapper verifyNoticeMapper;

    @Override
    public Long countNotice(Integer userId) {
        return verifyNoticeMapper.createLambdaQuery()
                .andEq(VerifyNotice::getReceiver, userId)
                .andEq(VerifyNotice::getState, NoticeState.UN_READ.value())
                .count();
    }

    @Override
    public List<ApplicationNoticeVO> listInvoiceNotice(Integer userId) {
        List<VerifyNotice> list = verifyNoticeMapper.createLambdaQuery()
                .andEq(VerifyNotice::getReceiver, userId)
                .andEq(VerifyNotice::getState, NoticeState.UN_READ.value())
                .desc(VerifyNotice::getCreateTime)
                .select();
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream()
                .map(applicationNotice -> ApplicationNoticeVO.builder()
                        .id(applicationNotice.getId())
                        .content(applicationNotice.getContent())
                        .batchNum(applicationNotice.getBatchNum())
                        .date(DateUtil.dateToString(applicationNotice.getCreateTime()))
                        .noticeType(NoticeType.CHECKER.value())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void changeNoticeState(Long id) {
        VerifyNotice verifyNotice = new VerifyNotice();
        verifyNotice.setId(id);
        verifyNotice.setState(1);
        verifyNoticeMapper.updateTemplateById(verifyNotice);
    }
}
