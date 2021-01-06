package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.publicity.enums.NoticeState;
import com.hbhb.cw.publicity.enums.NoticeType;
import com.hbhb.cw.publicity.mapper.ApplicationNoticeMapper;
import com.hbhb.cw.publicity.model.ApplicationNotice;
import com.hbhb.cw.publicity.rpc.FlowTypeApiExp;
import com.hbhb.cw.publicity.rpc.SysDictApiExp;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.rpc.UnitApiExp;
import com.hbhb.cw.publicity.service.ApplicationNoticeService;
import com.hbhb.cw.publicity.web.vo.ApplicationNoticeReqVO;
import com.hbhb.cw.publicity.web.vo.ApplicationNoticeResVO;
import com.hbhb.cw.publicity.web.vo.ApplicationNoticeVO;
import com.hbhb.cw.systemcenter.enums.DictCode;
import com.hbhb.cw.systemcenter.enums.TypeCode;
import com.hbhb.cw.systemcenter.vo.DictVO;

import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2020-12-04
 */
@Service
@Slf4j
@SuppressWarnings(value = {"rawtypes"})
public class ApplicationNoticeServiceImpl implements ApplicationNoticeService {


    @Resource
    private ApplicationNoticeMapper applicationNoticeMapper;
    @Resource
    private SysUserApiExp userApi;
    @Resource
    private SysDictApiExp dictApi;
    @Resource
    private FlowTypeApiExp typeApi;
    @Resource
    private UnitApiExp unitApi;

    @Override
    public void saveApplicationNotice(ApplicationNoticeResVO applicationNoticeVO) {
        ApplicationNotice notice = new ApplicationNotice();
        BeanUtils.copyProperties(applicationNoticeVO, notice);
        notice.setCreateTime(new Date());
        applicationNoticeMapper.insert(notice);
    }

    @Override
    public void updateByBatchNum(String batchNum) {
        applicationNoticeMapper.createLambdaQuery()
                .andEq(ApplicationNotice::getBatchNum,batchNum)
                .updateSelective(ApplicationNotice.builder().state(1).build());
    }

    @Override
    public Long countNotice(Integer userId) {
        return applicationNoticeMapper.createLambdaQuery()
                .andEq(ApplicationNotice::getReceiver, userId)
                .andEq(ApplicationNotice::getState, NoticeState.UN_READ.value())
                .count();
    }

    @Override
    public List<ApplicationNoticeVO> listInvoiceNotice(Integer userId) {
        List<ApplicationNotice> list = applicationNoticeMapper.createLambdaQuery()
                .andEq(ApplicationNotice::getReceiver, userId)
                .andEq(ApplicationNotice::getState, NoticeState.UN_READ.value())
                .desc(ApplicationNotice::getCreateTime)
                .select();
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<Integer> userIds = list.stream().map(ApplicationNotice::getPromoter).collect(Collectors.toList());
        Map<Integer, String> userMap = userApi.getUserMapById(userIds);
        return list.stream()
                .map(applicationNotice -> ApplicationNoticeVO.builder()
                        .id(applicationNotice.getId())
                        .content(applicationNotice.getContent())
                        .batchNum(applicationNotice.getBatchNum())
                        .date(DateUtil.dateToString(applicationNotice.getCreateTime()))
                        .userName(userMap.get(applicationNotice.getPromoter()))
                        .noticeType(NoticeType.MATERIALS.value())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<ApplicationNoticeResVO> pagePrintNotice(ApplicationNoticeReqVO cond, Integer pageNum, Integer pageSize) {
        PageRequest request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<ApplicationNoticeResVO> page = applicationNoticeMapper.selectPageByCond(cond, request);

        // 项目状态字典
        List<DictVO> stateList = dictApi.getDict(TypeCode.FUND.value(), DictCode.FUND_INVOICE_STATUS.value());
        Map<String, String> stateMap = stateList.stream().collect(
                Collectors.toMap(DictVO::getValue, DictVO::getLabel));

        // 组装
        page.getList().forEach(item -> {
            item.setStateLabel(stateMap.get(item.getState().toString()));
            item.setFlowType(typeApi.getNameById(item.getFlowTypeId()));
            item.setUnitName(unitApi.getUnitInfo(item.getUnitId()).getUnitName());
            item.setNoticeType(NoticeType.MATERIALS.value());
        });
        return page;
    }

    @Override
    public void changeNoticeState(Long id) {
        ApplicationNotice notice = new ApplicationNotice();
        notice.setId(id);
        notice.setState(1);
        applicationNoticeMapper.updateTemplateById(notice);
    }
}
