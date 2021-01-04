package com.hbhb.cw.publicity.service.impl;

import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.publicity.enums.NoticeState;
import com.hbhb.cw.publicity.enums.NoticeType;
import com.hbhb.cw.publicity.mapper.PictureNoticeMapper;
import com.hbhb.cw.publicity.model.PictureNotice;
import com.hbhb.cw.publicity.rpc.FlowTypeApiExp;
import com.hbhb.cw.publicity.rpc.SysDictApiExp;
import com.hbhb.cw.publicity.rpc.SysUserApiExp;
import com.hbhb.cw.publicity.rpc.UnitApiExp;
import com.hbhb.cw.publicity.service.PictureNoticeService;
import com.hbhb.cw.publicity.web.vo.NoticeReqVO;
import com.hbhb.cw.publicity.web.vo.NoticeResVO;
import com.hbhb.cw.publicity.web.vo.NoticeVO;
import com.hbhb.cw.systemcenter.enums.DictCode;
import com.hbhb.cw.systemcenter.enums.TypeCode;
import com.hbhb.cw.systemcenter.vo.DictVO;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wangxiaogang
 */
@Service
@Slf4j
@SuppressWarnings(value = {"rawtypes"})
public class PictureNoticeServiceImpl implements PictureNoticeService {

    @Resource
    private PictureNoticeMapper noticeMapper;
    @Resource
    private SysUserApiExp userApi;
    @Resource
    private SysDictApiExp dictApi;
    @Resource
    private FlowTypeApiExp typeApi;
    @Resource
    private UnitApiExp unitApi;

    @Override
    public void addPictureNotice(PictureNotice build) {
        noticeMapper.insert(build);
    }

    @Override
    public List<NoticeVO> listInvoiceNotice(Integer userId) {
        List<PictureNotice> list = noticeMapper.createLambdaQuery()
                .andEq(PictureNotice::getReceiver, userId)
                .andEq(PictureNotice::getState, NoticeState.UN_READ.value())
                .desc(PictureNotice::getCreateTime)
                .select();
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<Integer> userIds = list.stream().map(PictureNotice::getPromoter).collect(Collectors.toList());
        Map<Integer, String> userMap = userApi.getUserMapById(userIds);
        return list.stream()
                .map(notice -> NoticeVO.builder()
                        .id(notice.getId())
                        .content(notice.getContent())
                        .businessId(notice.getPictureId())
                        .date(DateUtil.dateToString(notice.getCreateTime()))
                        .userName(userMap.get(notice.getPromoter()))
                        .noticeType(NoticeType.PICTURE.value())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<NoticeResVO> pagePrintNotice(NoticeReqVO cond,
                                                   Integer pageNum, Integer pageSize) {
        PageRequest request = DefaultPageRequest.of(pageNum, pageSize);
        PageResult<NoticeResVO> page = noticeMapper.selectPageByCond(cond, request);

        // 项目状态字典
        List<DictVO> stateList = dictApi.getDict(TypeCode.FUND.value(), DictCode.FUND_INVOICE_STATUS.value());
        Map<String, String> stateMap = stateList.stream().collect(
                Collectors.toMap(DictVO::getValue, DictVO::getLabel));

        // 组装
        page.getList().forEach(item -> {
            item.setStateLabel(stateMap.get(item.getState().toString()));
            item.setFlowType(typeApi.getNameById(item.getFlowTypeId()));
            item.setUnitName(unitApi.getUnitInfo(item.getUnitId()).getUnitName());
            item.setNoticeType(NoticeType.PICTURE.value());
        });

        return page;
    }

    @Override
    public void changeNoticeState(Long id) {
        PictureNotice notice = new PictureNotice();
        notice.setId(id);
        notice.setState(1);
        noticeMapper.updateTemplateById(notice);
    }

    @Override
    public Long countNotice(Integer userId) {
        return noticeMapper.createLambdaQuery()
                .andEq(PictureNotice::getReceiver, userId)
                .andEq(PictureNotice::getState, NoticeState.UN_READ.value())
                .count();
    }
}
